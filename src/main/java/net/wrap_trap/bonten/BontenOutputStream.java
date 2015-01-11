package net.wrap_trap.bonten;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BontenOutputStream implements AutoCloseable {

  private DataOutputStream internal;

  public BontenOutputStream(final OutputStream os) {
    this.internal = new DataOutputStream(os);
  }

  public void writeShort(final short s) throws IOException {
    this.internal.writeShort(s);
  }

  public void writeInt(final int i) throws IOException {
    this.internal.writeInt(i);
  }
  
  public void writeTimestamp(final long l) throws IOException {
    // write last 4bytes
    this.internal.writeInt((int) l);
  }

  public void writeLong(final long l) throws IOException {
    this.internal.writeLong(l);
  }

  public void writeByte(final byte b) throws IOException {
    this.internal.writeByte(b);
  }

  public void write(final byte[] bytes) throws IOException {
    this.internal.write(bytes);
  }

  public void writeEndTag() throws IOException {
    writeByte((byte) 0xFF);
  }

  @Override
  public void close() throws IOException {
    try {
      this.internal.close();
    } catch (final Exception ignore) {}
  }
}
