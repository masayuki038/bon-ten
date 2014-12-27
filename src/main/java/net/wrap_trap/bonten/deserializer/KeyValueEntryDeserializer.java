package net.wrap_trap.bonten.deserializer;

import static net.wrap_trap.bonten.Bonten.SIZE_OF_ENTRY_TYPE;
import static net.wrap_trap.bonten.Bonten.SIZE_OF_KEYSIZE;
import static net.wrap_trap.bonten.Bonten.SIZE_OF_TIMESTAMP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import net.wrap_trap.bonten.BontenInputStream;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;

public class KeyValueEntryDeserializer extends AbstractDeserializer {

  public KeyValueEntryDeserializer(final boolean readTimestamp) {
    super(readTimestamp);
  }

  @Override
  public Entry deserialize(final byte[] body) throws IOException {
    final byte[] target = Arrays.copyOfRange(body, 1, body.length);
    try (BontenInputStream bis = new BontenInputStream(new ByteArrayInputStream(target))) {
      Date timestamp = null;
      if (isReadTimestamp()) {
        timestamp = new Date(bis.readUnsignedInt() * 1000L);
      }
      final long keyLen = bis.readUnsignedInt();
      final byte[] key = bis.read(keyLen);
      final long readSize = SIZE_OF_ENTRY_TYPE + ((isReadTimestamp()) ? SIZE_OF_TIMESTAMP : 0) + SIZE_OF_KEYSIZE +
                            keyLen;
      final byte[] value = bis.read(body.length - readSize);
      return new KeyValueEntry(key, value, timestamp);
    }
  }
}
