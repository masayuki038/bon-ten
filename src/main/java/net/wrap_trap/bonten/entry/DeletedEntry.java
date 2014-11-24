package net.wrap_trap.bonten.entry;

import java.nio.charset.Charset;
import java.util.Date;

import net.wrap_trap.bonten.Utils;

public class DeletedEntry extends Entry {

	private Date timestamp;
	
	public DeletedEntry(byte[] key) {
		this(key, null);
	}
	
	public DeletedEntry(byte[] key, Date timestamp) {
		super(key);
		this.timestamp = timestamp;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public byte[] getValue() {
		return Utils.toBytes("deleted");
	}
}
