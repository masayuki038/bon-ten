package net.wrap_trap.bonten.deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import net.wrap_trap.bonten.BontenInputStream;
import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;

import static net.wrap_trap.bonten.Bonten.SIZE_OF_ENTRY_TYPE;
import static net.wrap_trap.bonten.Bonten.SIZE_OF_TIMESTAMP;


public class DeletedEntryDeserializer extends AbstractDeserializer {

	public DeletedEntryDeserializer(boolean readTimestamp) {
		super(readTimestamp);
	}

	@Override
	public Entry deserialize(byte[] body) throws IOException {
		byte[] target = Arrays.copyOfRange(body, 1, body.length);
		try(BontenInputStream bis = new BontenInputStream(new ByteArrayInputStream(target))) {
			Date timestamp = null;
			if(isReadTimestamp()) {
				timestamp  = new Date(bis.readUnsignedInt() * 1000L);
			}
			int readSize = SIZE_OF_ENTRY_TYPE + ((isReadTimestamp())? SIZE_OF_TIMESTAMP : 0);
			byte[] key = bis.read(body.length - readSize);
			return new DeletedEntry(key, timestamp);
		}
	}
}
