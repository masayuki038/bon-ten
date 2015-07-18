package net.wrap_trap.bonten.message;

public class SetMaxLevel extends Message {

  private int max;

  public SetMaxLevel(int max) {
    super();
    this.max = max;
  }

  public int getMax() {
    return max;
  }
}
