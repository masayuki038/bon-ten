package net.wrap_trap.bonten.entry;

import java.util.Date;

public class DeletedEntry extends Entry {

  private Date timestamp;

  public DeletedEntry(final byte[] key) {
    this(key, null);
  }

  public DeletedEntry(final byte[] key, final Date timestamp) {
    super(key);
    if (timestamp != null) {
      this.timestamp = new Date(timestamp.getTime() / 1000L * 1000L);
    }
  }

  @Override
  public Date getTimestamp() {
    return this.timestamp;
  }

  @Override
  public byte[] getValue() {
    return TOMBSTONE;
  }
}
