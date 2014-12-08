package net.wrap_trap.bonten.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import net.wrap_trap.bonten.BontenOutputStream;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;
import static net.wrap_trap.bonten.Bonten.TAG_KV_DATA;
import static net.wrap_trap.bonten.Bonten.TAG_KV_DATA2;

public class KeyValueEntrySerializer implements Serializer {

	@Override
	public byte[] serialize(Entry entry) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(BontenOutputStream bos = new BontenOutputStream(baos)) {
			if(!(entry instanceof KeyValueEntry)) {
				throw new IllegalArgumentException("entry class expected KeyValueEntry, but :" + entry.getClass().getName());
			}
			KeyValueEntry kvEntry = (KeyValueEntry)entry;
			Date timestamp = kvEntry.getTimestamp();
			if(timestamp != null) {
				bos.writeByte((byte)TAG_KV_DATA2);
				bos.writeUnsignedInt(timestamp.getTime() / 1000L);
			} else {
				bos.writeByte((byte)TAG_KV_DATA);
			}
			bos.writeUnsignedInt(kvEntry.getKey().length);
			bos.write(kvEntry.getKey());
			bos.write(kvEntry.getValue());
			return baos.toByteArray();
		}
	}

}
