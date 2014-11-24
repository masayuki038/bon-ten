package net.wrap_trap.bonten.entry;

import java.util.Date;

abstract public class Entry {
	
	private byte[] key;

	public abstract byte[] getValue();
	
	public abstract Date getTimestamp();
	
	public Entry(byte[] key) {
		this.key = key;
	}

	public byte[] getKey() {
		return key;
	}
	
	public boolean expired() {
		Date timestamp = getTimestamp();
		if(timestamp == null) {
			return false;
		}
		return (timestamp.getTime() < System.currentTimeMillis());
	}
	
	public int estimateNodeSizeIncrement() {
		byte[] value = getValue();
		int valueSize = (value != null)? value.length : 0;
		return key.length + 5 + 4 + valueSize;
	}
}
