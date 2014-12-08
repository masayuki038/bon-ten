package net.wrap_trap.bonten;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

public class BontenInputStream implements AutoCloseable {
	
	private DataInputStream internal;
	
	public BontenInputStream(InputStream is) {
		this.internal = new DataInputStream(is);
	}
	
	public int read() throws IOException {
		return internal.read();
	}
		
	public long readUnsignedInt() throws IOException {
		return UnsignedInteger.fromIntBits(internal.readInt()).longValue();
	}
	
	public BigInteger readUnsignedLong() throws IOException {
		return UnsignedLong.fromLongBits(internal.readLong()).bigIntegerValue();
	}
	
	public byte[] read(long size) throws IOException {
		if(size > Integer.MAX_VALUE) {
			throw new IllegalStateException("size > Integer.MAX_VALUE");
		}
		byte[] buf = new byte[(int)size];
		int read = internal.read(buf);
		if(read < size) {
			throw new EOFException(String.format("read: %d, size: %d", read, size));
		}
		return buf;
	}
	
	public void readEndTag() throws IOException {
		int ch1 = internal.read();
		if(ch1 < 0) {
			throw new EOFException();
		}
		if(ch1 != 0xff) {
			throw new IllegalStateException("endTag != 0xff");
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			internal.close();
		} catch(IOException ignore) {}
	}
}
