package net.wrap_trap.bonten.range;

import java.util.List;

import net.wrap_trap.bonten.Tuple;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;

public class RangeFolderFuncResult {
  private RangeFolderFuncResultType type;
  private List<Entry> results;
  private byte[] lastKey;

  public RangeFolderFuncResult(RangeFolderFuncResultType type, List<Entry> results) {
    this(type, results, null);
  }
  
  public RangeFolderFuncResult(RangeFolderFuncResultType type, List<Entry> results, byte[] lastKey) {
    super();
    this.type = type;
    this.results = results;
    this.lastKey = lastKey;
  }

  public RangeFolderFuncResultType getType() {
    return type;
  }

  public List<Entry> getResults() {
    return results;
  }

  public byte[] getLastKey() {
    return lastKey;
  }
  
  public enum RangeFolderFuncResultType {
    LIMIT, DONE
  }
}
