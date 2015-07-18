package net.wrap_trap.bonten.message;

public class MergeDone extends Message {

  private int count;
  private String outFileName;
  
  public MergeDone(int count, String outFileName) {
    super();
    this.count = count;
    this.outFileName = outFileName;
  }
  
  public int getCount() {
    return count;
  }
  public String getOutFileName() {
    return outFileName;
  }
}
