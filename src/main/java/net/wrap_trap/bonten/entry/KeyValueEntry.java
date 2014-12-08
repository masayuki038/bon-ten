package net.wrap_trap.bonten.entry;

import java.util.Date;

public class KeyValueEntry extends Entry {

	private byte[] value;
	private Date timestamp;
	
	public KeyValueEntry(byte[] key, byte[] value) {
		this(key, value, null);
	}
	
	public KeyValueEntry(byte[] key, byte[] value, Date timestamp) {
		super(key);
		this.value = value;
		if(timestamp != null) {
			this.timestamp = new Date(timestamp.getTime() / 1000L * 1000L);
		}
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}
}
