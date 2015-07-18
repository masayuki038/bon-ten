package net.wrap_trap.bonten.message;

public class BeginIncrementalMerge extends Message {
  
  private int stepSize;

  public BeginIncrementalMerge(int stepSize) {
    super();
    this.stepSize = stepSize;
  }

  public int getStepSize() {
    return stepSize;
  }
}
