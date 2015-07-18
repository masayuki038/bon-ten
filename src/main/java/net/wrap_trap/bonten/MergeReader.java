package net.wrap_trap.bonten;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import net.wrap_trap.bonten.entry.Entry;

public class MergeReader {

  private String dataFilePath;
  private File dataFile;
  private DataInputStream fileReader;

  public MergeReader(final String dataFilePath) {
    this.dataFilePath = dataFilePath;
  }
  
  public void open() throws IOException {
    this.dataFile = new File(this.dataFilePath);
    if(!this.dataFile.exists()) {
      throw new FileNotFoundException("File: " + this.dataFilePath);
    }
    
    final BontenConfig config = Bonten.getConfig();
    int bufferSize = config.getInt("read_biffer_size", 512 * 1024);
    this.fileReader = new DataInputStream(new BufferedInputStream(new FileInputStream(this.dataFilePath), bufferSize));
  }
  
  public List<Entry> getFirstNode() throws IOException {
    Node node = readNode(Bonten.FIRST_BLOCK_POS);
    if(node.getLevel() != 0) {
      throw new IllegalStateException("Unexpected level: " + node.getLevel());
    }
    return node.getEntryList();
  }
  
  protected Node readNode(long pos) throws IOException {
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
