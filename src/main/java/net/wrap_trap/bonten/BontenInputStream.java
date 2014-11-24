package net.wrap_trap.bonten;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BontenInputStream implements AutoCloseable {
	
	private InputStream internal;
	
	public BontenInputStream(InputStream is) {
		this.internal = is;
	}
	
	public int read() throws IOException {
		return internal.read();
	}
		
	public long read4ByteToLong() throws IOException {
		int ch1 = internal.read();
		int ch2 = internal.read();
		int ch3 = internal.read();
		int ch4 = internal.read();
		if((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return (long)((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4); 
	}
	
	public long read8ByteToLong() throws IOException {
		int ch1 = internal.read();
		int ch2 = internal.read();
		int ch3 = internal.read();
		int ch4 = internal.read();
		int ch5 = internal.read();
		int ch6 = internal.read();
		int ch7 = internal.read();
		int ch8 = internal.read();
		if((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
			throw new EOFException();
		}
		return (((long)ch1 << 56) + 
				((long)(ch2 & 255) << 48) + 
				((long)(ch3 & 255) << 40) + 
				((long)(ch4 & 255) << 32) + 
				((long)(ch5 & 255) << 24) + 
				((ch6 & 255) << 16) + 
				((ch7 & 255) << 8) + 
				((ch8 & 255) << 0)); 
				
	}
	
	public byte[] read(long size) throws IOException {
		if(size > Integer.MAX_VALUE) {
			throw new IllegalStateException("size > Integer.MAX_VALUE");
		}
		byte[] buf = new byte[(int)size];
		if(internal.read(buf) < size) {
			throw new EOFException();
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
