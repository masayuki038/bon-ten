package net.wrap_trap.bonten.serializer;

import static net.wrap_trap.bonten.Bonten.TAG_DELETED;
import static net.wrap_trap.bonten.Bonten.TAG_DELETED2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import net.wrap_trap.bonten.BontenOutputStream;
import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;

public class DeletedEntrySerializer implements Serializer {

	@Override
	public byte[] serialize(Entry entry) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(BontenOutputStream bos = new BontenOutputStream(baos)) {
			if(!(entry instanceof DeletedEntry)) {
				throw new IllegalArgumentException("entry class expected DeletedEntry, but :" + entry.getClass().getName());
			}
			DeletedEntry deletedEntry = (DeletedEntry)entry;
			Date timestamp = deletedEntry.getTimestamp();
			if(timestamp != null) {
				bos.writeByte((byte)TAG_DELETED2);
				bos.writeUnsignedInt(timestamp.getTime() / 1000L);
			} else {
				bos.writeByte((byte)TAG_DELETED);
			}
			bos.write(deletedEntry.getKey());
			return baos.toByteArray();
		}
	}
}
