package net.wrap_trap.bonten;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class BontenOutputStream implements AutoCloseable {

	private DataOutputStream internal;
	
	public BontenOutputStream(OutputStream os) {
		this.internal = new DataOutputStream(os);
	}

	public void writeUnsignedInt(long l) throws IOException {
		// write last 4bytes
		this.internal.writeInt((int)l);
	}
	
	public void writeUnsignedShort(int i) throws IOException {
		// write last 2bytes
		this.internal.writeShort(i);
	}
	
	public void writeUnsignedLong(BigInteger b) throws IOException {
		// write last 8bytes
		this.internal.writeLong(b.longValue());
	}
	
	public void writeInt(int i) throws IOException {
		this.internal.writeInt(i);
	}
	
	public void writeByte(byte b) throws IOException {
		this.internal.writeByte(b);
	}

	public void write(byte[] bytes) throws IOException {
		this.internal.write(bytes);
	}
	
	public void writeEndTag() throws IOException {
		writeByte((byte)0xFF);
	}

	@Override
	public void close() throws IOException {
		try {
			this.internal.close();
		} catch(Exception ignore) {}
	}
}
