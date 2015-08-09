package net.wrap_trap.bonten.message;

public class TotalUnmerged extends Message {
  private int totalUnmerged;

  public TotalUnmerged(int totalUnmerged) {
    super();
    this.totalUnmerged = totalUnmerged;
  }

  public int getTotalUnmerged() {
    return totalUnmerged;
  }
}
