package net.wrap_trap.bonten.deserializer;

import static net.wrap_trap.bonten.Bonten.SIZE_OF_ENTRY_TYPE;
import static net.wrap_trap.bonten.Bonten.SIZE_OF_LEN;
import static net.wrap_trap.bonten.Bonten.SIZE_OF_POS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import net.wrap_trap.bonten.BontenInputStream;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.PosLenEntry;

public class PosLenEntryDeserializer extends AbstractDeserializer {

  public PosLenEntryDeserializer() {
    super(false);
  }

  @Override
  public Entry deserialize(final byte[] body) throws IOException {
    final byte[] target = Arrays.copyOfRange(body, 1, body.length);
    try (BontenInputStream bis = new BontenInputStream(new ByteArrayInputStream(target))) {
      final BigInteger pos = bis.readUnsignedLong();
      final long len = bis.readUnsignedInt();
      final int readSize = SIZE_OF_ENTRY_TYPE + SIZE_OF_POS + SIZE_OF_LEN;
      final byte[] key = bis.read(body.length - readSize);
      return new PosLenEntry(key, pos, len);
    }
  }
}
