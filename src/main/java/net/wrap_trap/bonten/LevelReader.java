package net.wrap_trap.bonten;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedBytes;

import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;
import net.wrap_trap.bonten.entry.PosLenEntry;
import net.wrap_trap.bonten.range.FoldUntilStopFuncResult;
import net.wrap_trap.bonten.range.FoldUntilStopFuncResult.FoldUntilStopFuncResultType;
import net.wrap_trap.bonten.range.FoldUntilStopFunction;
import net.wrap_trap.bonten.range.RangeFolderFuncResult;
import net.wrap_trap.bonten.range.RangeFolderFuncResult.RangeFolderFuncResultType;
import net.wrap_trap.bonten.range.RangeFolderFunction;

public class LevelReader {

  private File dataFile;
  private Node root;
  private Bloom bloom;
  private RandomAccessFile randomReader;
  
  public LevelReader(final String dataFilePath) {
    this(new File(dataFilePath));
  }
  
  public LevelReader(final File file) {
    this.dataFile = file;
  }
  
  public void open() throws IOException {
    if(!this.dataFile.exists()) {
      throw new FileNotFoundException("File: " + dataFile.getCanonicalPath());
    }
    
    this.randomReader = new RandomAccessFile(dataFile.getCanonicalFile(), "r");
    prepareForRandom();
  }
  
  protected void prepareForRandom() throws FileNotFoundException, IOException {
    try {
      int length = Utils.toBytes(Bonten.FILE_FORMAT).length;
      byte[] formatBuf = new byte[length];
      this.randomReader.read(formatBuf, 0, length);
      String format = Utils.toString(formatBuf);
      if(!Bonten.FILE_FORMAT.equals(format)) {
        throw new IllegalStateException("Invalid file format: " + format);
      }
      
      this.randomReader.seek(this.dataFile.length() - 8);
      long rootPos = this.randomReader.readLong();
      
      this.randomReader.seek(this.dataFile.length() - 12);
      int bloomSize = this.randomReader.readInt();
      byte[] bloomBuffer = new byte[bloomSize];
      this.randomReader.seek(this.dataFile.length() - 12 - bloomSize);
      this.randomReader.read(bloomBuffer);
      this.bloom = deserializeBloom(bloomBuffer);
      
      this.root = readNode(rootPos);
    } catch(ClassNotFoundException e) {
      throw new IOException(e);
    }
  }
  
  public void close() throws IOException {
    this.randomReader.close();
  }
  
  public void destroy() throws IOException {
    this.randomReader.close();
    this.dataFile.delete();
  }
  
  public RangeFolderFuncResult rangeFold(RangeFolderFunction func, KeyRange range) throws IOException {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    if(comparator.compare(range.getFromKey(), firstKey()) < 0) {
      randomReader.seek(Bonten.FIRST_BLOCK_POS);
    } else {
      PosLenEntry posLenEntry = findLeafNode(range.getFromKey(), this.root, new PosLenEntry(Bonten.dummyKey, Bonten.FIRST_BLOCK_POS, 0));
      randomReader.seek(posLenEntry.getPos());      
    }
    return rangeFoldFromHere(func, range, range.getLimit());
  }
  
  protected RangeFolderFuncResult rangeFoldFromHere(RangeFolderFunction func, KeyRange range) throws IOException {
    List<Entry> acc1 = Lists.newArrayList();
    Node nextLeafNode = null;
    while((nextLeafNode = nextLeafNode()) != null) {
      for(Entry entry : nextLeafNode.getEntryList()) {
        if(!keyInToRange(entry.getKey(), range)) {
          break;
        }
        if(keyInFromRange(entry.getKey(), range)) {
          if(!entry.hasExpired()) {
            func.apply(entry);
          }
        }
      }
    }
    return new RangeFolderFuncResult(RangeFolderFuncResultType.DONE, acc1);
  }
  
  protected RangeFolderFuncResult rangeFoldFromHere(RangeFolderFunction func, KeyRange range, int limit) throws IOException {
    int remain = limit;
    List<Entry> acc1 = Lists.newArrayList();
    Node nextLeafNode = null;
    while((nextLeafNode = nextLeafNode()) != null) {
      for(Entry entry : nextLeafNode.getEntryList()) {
        if(remain == 0) {
          return new RangeFolderFuncResult(RangeFolderFuncResultType.LIMIT, acc1, entry.getKey());    
        }
        if(!keyInToRange(entry.getKey(), range)) {
          break;
        }
        if(keyInFromRange(entry.getKey(), range)) {
          if(!entry.hasExpired()) {
            func.apply(entry);
            if(!entry.isTombstone()) {
              remain --;
            }
          }
        }
      }
    }
    return new RangeFolderFuncResult(RangeFolderFuncResultType.DONE, acc1);    
  }

  protected boolean keyInFromRange(byte[] key, KeyRange range) {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    return ( (range.isFromInclusive() && comparator.compare(key, range.getFromKey()) >= 0) ||
             (comparator.compare(key, range.getFromKey()) > 0));
  }

  protected boolean keyInToRange(byte[] key, KeyRange range) {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    return ((range.getToKey() == null) || 
             (range.isToInclusive() && comparator.compare(key, range.getToKey()) <= 0) ||
             (comparator.compare(key, range.getToKey()) < 0));
  }
   
  protected Node nextLeafNode() throws IOException {
    while(this.randomReader.length() - this.randomReader.getFilePointer() >= 6) {    
      int len = this.randomReader.readInt();
      short level = this.randomReader.readShort();
      if(len == 0) {
        return null;
      } 
      if(level == 0) {
        byte[] buf = new byte[len - 2];
        this.randomReader.read(buf);
        return new Node(level, Utils.decodeIndexNodes(buf));
      }
      this.randomReader.skipBytes(len - 2);
    }
    return null;
  }
  
  protected PosLenEntry findLeafNode(byte[] fromKey, Node node, PosLenEntry pos) throws IOException {
    if(node.getLevel() == 0) {
      return pos;
    }
    PosLenEntry childPos = find1(node, fromKey);
    if(childPos != null) {
      return recursiveFind(fromKey, node.getLevel(), childPos);
    }
    return null;
  }
  
  protected PosLenEntry recursiveFind(byte[] fromKey, int level, PosLenEntry childPos) throws IOException {
    if(level == 1) {
      return childPos;
    }
    Node childNode = readNode(childPos);
    if(childNode != null) {
      return findLeafNode(fromKey, childNode, childPos);
    }
    return null;
  }
  
  protected byte[] firstKey() {
    List<Entry> members = root.getEntryList();
    if(members.size() == 0) {
      throw new IllegalStateException("root has no member");
    }
    return members.get(0).getKey();
  }
  
  protected Bloom deserializeBloom(byte[] raw) throws IOException, ClassNotFoundException {
    try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(raw))) {
      return (Bloom)ois.readObject();
    }
  }
  
  protected Node readNode(long rootPos) throws IOException {
    this.randomReader.seek(rootPos);
    int len = this.randomReader.readInt();
    int level = this.randomReader.readShort();
    if(len == 0) {
      return null;
    }
    byte[] buf = new byte[len - 2];
    this.randomReader.read(buf);
    List<Entry> entryList = Utils.decodeIndexNodes(buf);
    return new Node(level, entryList);
  }
  
  protected Node readNode(PosLenEntry posLenEntry) throws IOException {
    this.randomReader.seek(posLenEntry.getPos() + 4); // skip 'len' field
    int level = this.randomReader.readShort();
    byte[] buf = new byte[posLenEntry.getLen() - 4 - 2];
    this.randomReader.read(buf);
    List<Entry> entryList = Utils.decodeIndexNodes(buf);
    return new Node(level, entryList);    
  }
  
  public Entry lookup(byte[] key) throws IOException {
    if(!this.bloom.member(key)) {
      // TODO return null to Option.NONE
      return null;
    }
    Entry entry = lookupInNode(key);
    if(entry == null) {
      // TODO return null to Option.NONE
      return null;
    }
    if(entry.hasExpired()) {
      // TODO return null to Option.NONE
      return null;
    }
    return entry;
  }
  
  protected Entry lookupInNode(byte[] key) throws IOException {
    if(this.root.getLevel() == 0) {
      return findInLeaf(key);
    }
    Entry entry = find1(key);
    if(!(entry instanceof PosLenEntry)) {
      throw new IllegalStateException("Unexpected Entry Type: " + entry.getClass().getSimpleName());
    }
    Node node = readNode((PosLenEntry)entry);
    return lookupInNode2(node, key);
  }
  
  protected Entry lookupInNode2(Node node, byte[] key) throws IOException {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    if(node.getLevel() == 0) {
      for(Entry entry : node.getEntryList()) {
        if(comparator.compare(key, entry.getKey()) == 0) {
          return entry;
        }
      }
      return null;
    }
    Entry nextEntry = find1(node, key);
    if(nextEntry == null) {
      return null;
    }
    if(!(nextEntry instanceof PosLenEntry)) {
      throw new IllegalStateException("Unexpected Entry Type: " + nextEntry.getClass().getSimpleName());
    }
    Node nextNode = readNode((PosLenEntry)nextEntry);
    return lookupInNode2(nextNode, key);
  }
  
  protected Entry findInLeaf(byte[] key) {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    for(Entry entry : this.root.getEntryList()) {
      if(comparator.compare(key, entry.getKey()) == 0) {
        return entry;
      }
    }
    return null;
  }
  
  protected PosLenEntry find1(byte[] key) {
    return find1(this.root, key);
  }
  
  protected PosLenEntry find1(Node node, byte[] key) {
    Comparator<byte[]> comparator = UnsignedBytes.lexicographicalComparator();
    if(node.getEntryList().size() == 1) {
      Entry first = node.getEntryList().get(0);
      if(!(first instanceof PosLenEntry)) {
        throw new IllegalStateException("Unexpected Entry Type: " + first.getClass().getSimpleName());
      }
      if(comparator.compare(key, first.getKey()) >= 0) {
        return (PosLenEntry)first;
      }
    } else {
      PosLenEntry ret = null;
      for(Entry entry : node.getEntryList()) {
        if(ret != null && comparator.compare(key, entry.getKey()) < 0) {
          return ret;
        }
        if(comparator.compare(key, entry.getKey()) >= 0) {
          if(!(entry instanceof PosLenEntry)) {
            throw new IllegalStateException("Unexpected Entry Type: " + entry.getClass().getSimpleName());
          }
          ret = (PosLenEntry)entry;
        }
      }      
    }
    return null; 
  }
}
