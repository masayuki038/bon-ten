package net.wrap_trap.bonten.entry;

import java.util.Date;

public class PosLenEntry extends Entry {

  private long pos;
  private int len;

  public PosLenEntry(final byte[] key, final long pos, final int len) {
    super(key);
    this.pos = pos;
    this.len = len;
  }

  public long getPos() {
    return this.pos;
  }

  public int getLen() {
    return this.len;
  }

  @Override
  public Date getTimestamp() {
    return null;
  }

  @Override
  public byte[] getValue() {
    return null;
  }
}
