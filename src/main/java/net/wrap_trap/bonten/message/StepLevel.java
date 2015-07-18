package net.wrap_trap.bonten.message;

public class StepLevel {

  private int workIncludingHere;
  private int stepSize;

  public StepLevel(int workIncludingHere, int stepSize) {
    super();
    this.workIncludingHere = workIncludingHere;
    this.stepSize = stepSize;
  }

  protected int getWorkIncludingHere() {
    return workIncludingHere;
  }

  protected int getStepSize() {
    return stepSize;
  }
}
