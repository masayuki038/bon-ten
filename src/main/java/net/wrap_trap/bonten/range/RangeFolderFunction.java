package net.wrap_trap.bonten.range;

import net.wrap_trap.bonten.entry.Entry;

@FunctionalInterface
public interface RangeFolderFunction {
  void apply(Entry entry);
}
