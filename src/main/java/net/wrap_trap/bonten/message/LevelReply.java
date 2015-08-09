package net.wrap_trap.bonten.message;

public class LevelReply extends Message {
  private int level;

  public LevelReply(int level) {
    super();
    this.level = level;
  }

  public int getLevel() {
    return level;
  }
}
