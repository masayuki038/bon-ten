package net.wrap_trap.bonten.serializer;

import static net.wrap_trap.bonten.Bonten.TAG_POSLEN;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.wrap_trap.bonten.BontenOutputStream;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.PosLenEntry;

public class PosLenEntrySerializer implements Serializer {

  @Override
  public byte[] serialize(final Entry entry) throws IOException {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (BontenOutputStream bos = new BontenOutputStream(baos)) {
      if (!(entry instanceof PosLenEntry))
        throw new IllegalArgumentException("entry class expected PosLenEntry, but :" + entry.getClass().getName());
      final PosLenEntry posLenEntry = (PosLenEntry) entry;
      bos.writeByte(TAG_POSLEN);
      bos.writeLong(posLenEntry.getPos());
      bos.writeInt(posLenEntry.getLen());
      bos.write(posLenEntry.getKey());
      return baos.toByteArray();
    }
  }
}
