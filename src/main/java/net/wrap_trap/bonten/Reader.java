package net.wrap_trap.bonten;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.List;

import net.wrap_trap.bonten.entry.Entry;

public class Reader {

  private String dataFilePath;
  private File dataFile;
  private DataInputStream fileReader;
  private Node node;
  private Bloom bloom;
  private RandomAccessFile randomReader;
  
  public Reader(final String dataFilePath) {
    this.dataFilePath = dataFilePath;
  }
  
  public void open(final Read read) throws IOException {
    this.dataFile = new File(this.dataFilePath);
    if(!this.dataFile.exists()) {
      throw new FileNotFoundException("File: " + this.dataFilePath);
    }
    
    final BontenConfig config = Bonten.getConfig();
    switch(read) {
    case SEQUENTIAL:
      int bufferSize = config.getInt("read_biffer_size", 512 * 1024);
      fileReader = new DataInputStream(new BufferedInputStream(new FileInputStream(this.dataFilePath), bufferSize));
      return;
    case RANDOM:
      this.randomReader = new RandomAccessFile(this.dataFilePath, "r");
      prepare();
      break;
    default:
      throw new IllegalArgumentException("Invalid Read: " + read);
    }    
  }
  
  public List<Entry> getFirstNode() throws IOException {
    Node node = readNode(Bonten.FIRST_BLOCK_POS);
    if(node.getLevel() != 0) {
      throw new IllegalStateException("Unexpected level: " + node.getLevel());
    }
    return node.getEntryList();
  }

  protected void prepare() throws FileNotFoundException, IOException {
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
      
      this.node = readNode(rootPos);
    } catch(ClassNotFoundException e) {
      throw new IOException(e);
    }
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
  
  protected Node readNodeBySequential(long pos) throws IOException {
    this.fileReader.skip(pos);
    int len = this.fileReader.readInt();
    int level = this.fileReader.readShort();
    if(len == 0) {
      return null;
    }
    byte[] buf = new byte[len - 2];
    this.fileReader.read(buf);
    List<Entry> entryList = Utils.decodeIndexNodes(buf);
    return new Node(level, entryList);
  }
}
