package net.wrap_trap.bonten;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import com.google.common.primitives.UnsignedBytes;

import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;
import net.wrap_trap.bonten.entry.PosLenEntry;

public class Utils {
	
	private static final int SIZE_OF_ENTRY_TYPE = 1;
	private static final int SIZE_OF_KEYSIZE = 4;
	private static final int SIZE_OF_TIMESTAMP = 4;
	private static final int SIZE_OF_POS = 8;
	private static final int SIZE_OF_LEN = 4;
	
	private static final int TAG_KV_DATA = 128;
	private static final int TAG_DELETED = 129;
	private static final int TAG_POSLEN = 130;
	private static final int TAG_TRANSACT = 131;
	private static final int TAG_KV_DATA2 = 132;
	private static final int TAG_DELETED2 = 133;

	public static void ensureExpiry() {
		BontenConfig config = Bonten.getConfig();
		int expirySecs = config.getInt("expiry_secs");
		if(expirySecs < 0) {
			throw new IllegalArgumentException(String.format("Invalid 'expiry_secs': %d", expirySecs));
		}
	}
	
	public static double log2(double e) {
		return Math.log(e) / Math.log(2);
	}
	
	public static Entry createEntry(byte[] body) throws IOException {
		try(BontenInputStream bis = new BontenInputStream(new ByteArrayInputStream(body))) {
			int type = bis.read();
			switch(type) {
				case TAG_KV_DATA:
					return createKeyValueEntry(body, bis, false);
				case TAG_DELETED:
					return createDeletedEntry(body, bis, false);
				case TAG_POSLEN:
					return createPosLenEntry(body, bis);
				case TAG_KV_DATA2:
					return createKeyValueEntry(body, bis, true);
				case TAG_DELETED2:
					return createDeletedEntry(body, bis, true);
				default:
					throw new IllegalStateException("Unexpected Entry Type: " + type);
			}
		}
	}
	
	public static int compareBytes(byte[] a, byte[] b) {
		return UnsignedBytes.lexicographicalComparator().compare(a, b);
	}
	
	public static byte[] toBytes(String str) {
		return str.getBytes(Charset.forName("UTF-8"));
	}

	private static KeyValueEntry createKeyValueEntry(byte[] body, BontenInputStream bis, boolean readTimestamp)
			throws IOException {
		Date timestamp = null;
		if(readTimestamp) {
			timestamp  = new Date(bis.read4ByteToLong());
		}
		long size = bis.read4ByteToLong();
		byte[] key = bis.read(size);
		long readSize = SIZE_OF_ENTRY_TYPE + ((readTimestamp)? 0 : SIZE_OF_TIMESTAMP) + SIZE_OF_KEYSIZE + size;
		byte[] value = bis.read(body.length - readSize);
		return new KeyValueEntry(key, value, timestamp);
	}
	
	private static DeletedEntry createDeletedEntry(byte[] body, BontenInputStream bis, boolean readTimestamp) throws IOException {
		Date timestamp = null;
		if(readTimestamp) {
			timestamp  = new Date(bis.read4ByteToLong());
		}
		int readSize = SIZE_OF_ENTRY_TYPE + ((readTimestamp)? 0 : SIZE_OF_TIMESTAMP);
		byte[] key = bis.read(body.length - readSize);
		return new DeletedEntry(key, timestamp);
	}
	
	private static PosLenEntry createPosLenEntry(byte[] body, BontenInputStream bis) throws IOException {
		long pos = bis.read8ByteToLong();
		long len = bis.read4ByteToLong();
		int readSize = SIZE_OF_POS + SIZE_OF_LEN;
		byte[] key = bis.read(body.length - readSize);
		return new PosLenEntry(key, pos, len);
	}
}
