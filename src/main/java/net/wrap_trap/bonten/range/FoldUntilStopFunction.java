package net.wrap_trap.bonten.range;

import java.util.List;

import net.wrap_trap.bonten.Tuple;
import net.wrap_trap.bonten.entry.KeyValueEntry;

public interface FoldUntilStopFunction {
  FoldUntilStopFuncResult apply(KeyValueEntry entry, Tuple<Integer, List<KeyValueEntry>> acc1);
}
