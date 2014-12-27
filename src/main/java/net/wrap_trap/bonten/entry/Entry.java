package net.wrap_trap.bonten.entry;

import java.util.Date;

import net.wrap_trap.bonten.Utils;

abstract public class Entry {

  public static final byte[] TOMBSTONE = Utils.toBytes("deleted");

  private byte[] key;

  public abstract byte[] getValue();

  public abstract Date getTimestamp();

  public Entry(final byte[] key) {
    this.key = key;
  }

  public byte[] getKey() {
    return this.key;
  }

  public boolean expired() {
    final Date timestamp = getTimestamp();
    if (timestamp == null)
      return false;
    return (timestamp.getTime() < System.currentTimeMillis());
  }

  public int estimateNodeSizeIncrement() {
    final byte[] value = getValue();
    final int valueSize = (value != null) ? value.length : 0;
    return this.key.length + 5 + 4 + valueSize;
  }

  public boolean isTombstone() {
    return getValue() == TOMBSTONE;
  }
}
