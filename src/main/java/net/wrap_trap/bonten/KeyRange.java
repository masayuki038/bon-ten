package net.wrap_trap.bonten;
import java.util.List;

public class KeyRange {

  private byte[] fromKey;
  private boolean fromInclusive = true;
  private byte[] toKey;
  private boolean toInclusive;
  private int limit;

  public void setFromKey(byte[] fromKey) {
    this.fromKey = fromKey;
  }

  public void setFromInclusive(boolean fromInclusive) {
    this.fromInclusive = fromInclusive;
  }

  public void setToKey(byte[] toKey) {
    this.toKey = toKey;
  }

  public void setToInclusive(boolean toInclusive) {
    this.toInclusive = toInclusive;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public byte[] getFromKey() {
    return fromKey;
  }

  public boolean isFromInclusive() {
    return fromInclusive;
  }

  public byte[] getToKey() {
    return toKey;
  }

  public boolean isToInclusive() {
    return toInclusive;
  }

  public int getLimit() {
    return limit;
  }
}
