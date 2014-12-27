package net.wrap_trap.bonten.entry;

import java.math.BigInteger;
import java.util.Date;

public class PosLenEntry extends Entry {

  private BigInteger pos;
  private long len;

  public PosLenEntry(final byte[] key, final BigInteger pos, final long len) {
    super(key);
    this.pos = pos;
    this.len = len;
  }

  public BigInteger getPos() {
    return this.pos;
  }

  public long getLen() {
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
