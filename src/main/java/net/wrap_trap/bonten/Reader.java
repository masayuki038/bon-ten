package net.wrap_trap.bonten;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
  private InputStream fileReader;
  private Node node;
  private Bloom bloom;
  
  public Reader(final String dataFilePath) {
    this.dataFilePath = dataFilePath;
  }
  
  public void open(final Read read) throws IOException, ClassNotFoundException {
    File file = new File(this.dataFilePath);
    if(!file.exists()) {
      throw new FileNotFoundException("File: " + this.dataFilePath);
    }
    
    final BontenConfig config = Bonten.getConfig();
    switch(read) {
    case SEQUENTIAL:
      int bufferSize = config.getInt("read_biffer_size", 512 * 1024);
      fileReader = new BufferedInputStream(new FileInputStream(this.dataFilePath), bufferSize);
      return;
    case RANDOM:
      prepare(file);
      // TODO Check that this.fileReader is used actually later. 
      fileReader = new FileInputStream(this.dataFilePath);
      break;
    default:
      throw new IllegalArgumentException("Invalid Read: " + read);
    }    
  }

  protected void prepare(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
    try(RandomAccessFile randomAccessFile = new RandomAccessFile(this.dataFilePath, "r")) {
      int length = Utils.toBytes(Bonten.FILE_FORMAT).length;
      byte[] formatBuf = new byte[length];
      randomAccessFile.read(formatBuf, 0, length);
      String format = Utils.toString(formatBuf);
      if(!Bonten.FILE_FORMAT.equals(format)) {
        throw new IllegalStateException("Invalid file format: " + format);
      }
      
      randomAccessFile.seek(file.length() - 8);
      long rootPos = randomAccessFile.readLong();
      
      randomAccessFile.seek(file.length() - 12);
      int bloomSize = randomAccessFile.readInt();
      byte[] bloomBuffer = new byte[bloomSize];
      randomAccessFile.seek(file.length() - 12 - bloomSize);
      randomAccessFile.read(bloomBuffer);
      this.bloom = deserializeBloom(bloomBuffer);
      
      this.node = readNode(randomAccessFile, rootPos);
    }
  }
  
  protected Bloom deserializeBloom(byte[] raw) throws IOException, ClassNotFoundException {
    try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(raw))) {
      return (Bloom)ois.readObject();
    }
  }
  
  protected Node readNode(RandomAccessFile randomAccessFile, long rootPos) throws IOException {
    randomAccessFile.seek(rootPos);
    int len = randomAccessFile.readInt();
    int level = randomAccessFile.readShort();
    if(len == 0) {
      return null;
    }
    byte[] buf = new byte[len - 2];
    randomAccessFile.read(buf);
    List<Entry> entryList = Utils.decodeIndexNodes(buf);
    return new Node(level, entryList);
  }
}
