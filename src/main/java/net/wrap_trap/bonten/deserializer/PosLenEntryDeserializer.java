package net.wrap_trap.bonten.deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import net.wrap_trap.bonten.BontenInputStream;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.PosLenEntry;

import static net.wrap_trap.bonten.Bonten.SIZE_OF_POS;
import static net.wrap_trap.bonten.Bonten.SIZE_OF_LEN;

public class PosLenEntryDeserializer extends AbstractDeserializer {

	public PosLenEntryDeserializer() {
		super(false);
	}

	@Override
	public Entry deserialize(byte[] body) throws IOException {
		byte[] target = Arrays.copyOfRange(body, 1, body.length-1);
		try(BontenInputStream bis = new BontenInputStream(new ByteArrayInputStream(target))) {
			BigInteger pos = bis.readUnsignedLong();
			long len = bis.readUnsignedInt();
			int readSize = SIZE_OF_POS + SIZE_OF_LEN;
			byte[] key = bis.read(body.length - readSize);
			return new PosLenEntry(key, pos, len);
		}
	}
}
