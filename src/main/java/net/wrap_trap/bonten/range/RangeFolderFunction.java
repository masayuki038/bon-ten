package net.wrap_trap.bonten.range;

import java.util.List;

import net.wrap_trap.bonten.entry.Entry;

@FunctionalInterface
public interface RangeFolderFunction {
  void apply(Entry entry);
}
