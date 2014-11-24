package net.wrap_trap.bonten.entry;

import java.util.Date;

public class PosLenEntry extends Entry {

	private long pos;
	private long len;
	
	public PosLenEntry(byte[] key, long pos, long len) {
		super(key);
		this.pos = pos;
		this.len = len;
	}

	public long getPos() {
		return pos;
	}

	public long getLen() {
		return len;
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
