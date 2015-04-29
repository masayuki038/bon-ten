package net.wrap_trap.bonten.file;

public interface DataFile {

  public void open(String dataFilePath);
  public long skip(long pos);
  public int readInt();
  public int readShort();
  public int read(byte[] buf);
}
