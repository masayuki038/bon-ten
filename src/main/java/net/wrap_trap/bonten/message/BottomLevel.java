package net.wrap_trap.bonten.message;

public class BottomLevel extends Message {

  private int level;

  public BottomLevel(int level) {
    super();
    this.level = level;
  }

  public int getLevel() {
    return level;
  }
}
