package net.wrap_trap.bonten.message;


public class Inject extends Message {

  private String filePath;
  
  public Inject(String filePath) {
    super();
    this.filePath = filePath;
  }
  public String getFilePath() {
    return filePath;
  } 
}