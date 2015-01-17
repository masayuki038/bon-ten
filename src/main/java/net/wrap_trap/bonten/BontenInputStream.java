package net.wrap_trap.bonten;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.primitives.UnsignedInteger;

public class BontenInputStream implements AutoCloseable {

  private DataInputStream internal;

  public BontenInputStream(final InputStream is) {
    this.internal = new DataInputStream(is);
  }

  public int read() throws IOException {
    return this.internal.read();
  }
  
  public int read(byte[] bytes) throws IOException {
    return this.internal.read(bytes);
  }

  public int readInt() throws IOException {
    return this.internal.readInt();
  }
  
  public long readTimestamp() throws IOException {
    return UnsignedInteger.fromIntBits(this.internal.readInt()).longValue();
  }

  public long readLong() throws IOException {
    return this.internal.readLong();
  }

  public byte[] read(final long size) throws IOException {
    if (size > Integer.MAX_VALUE)
      throw new IllegalStateException("size > Integer.MAX_VALUE");
    final byte[] buf = new byte[(int) size];
    final int read = this.internal.read(buf);
    if (read < size)
      throw new EOFException(String.format("read: %d, size: %d", read, size));
    return buf;
  }

  public void readEndTag() throws IOException {
    final int ch1 = this.internal.read();
    if (ch1 < 0)
      throw new EOFException();
    if (ch1 != 0xff)
      throw new IllegalStateException("endTag != 0xff");
  }

  @Override
  public void close() throws IOException {
    try {
      this.internal.close();
    } catch (final IOException ignore) {}
  }
}
