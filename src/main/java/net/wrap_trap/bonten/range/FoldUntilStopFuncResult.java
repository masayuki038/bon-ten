package net.wrap_trap.bonten.range;

import java.util.List;

import net.wrap_trap.bonten.Tuple;
import net.wrap_trap.bonten.entry.KeyValueEntry;

public class FoldUntilStopFuncResult {

  private FoldUntilStopFuncResultType type;
  private Tuple<Integer, List<KeyValueEntry>> acc;
  
  public FoldUntilStopFuncResult(FoldUntilStopFuncResultType type, Tuple<Integer, List<KeyValueEntry>> acc) {
    super();
    this.type = type;
    this.acc = acc;
  }
  
  public FoldUntilStopFuncResultType getType() {
    return type;
  }

  public Tuple<Integer, List<KeyValueEntry>> getAcc() {
    return acc;
  }

  public enum FoldUntilStopFuncResultType {
    STOP, CONTINUE
  }
}
