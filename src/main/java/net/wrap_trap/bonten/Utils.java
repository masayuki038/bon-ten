package net.wrap_trap.bonten;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32;

import com.google.common.primitives.UnsignedBytes;

import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.serializer.Serializer;
import net.wrap_trap.bonten.serializer.SerializerFactory;

public class Utils {

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
	
	public static int compareBytes(byte[] a, byte[] b) {
		return UnsignedBytes.lexicographicalComparator().compare(a, b);
	}
	
	public static byte[] toBytes(String str) {
		return str.getBytes(Charset.forName("UTF-8"));
	}
	
	public static boolean checkCRC(long crc, byte[] body) {
		CRC32 crc32 = getCRC(body);
		return (crc == crc32.getValue());
	}

	public static long getCRCValue(byte[] body) {
		CRC32 crc32 = getCRC(body);
		return crc32.getValue();
	}
	
	private static CRC32 getCRC(byte[] body) {
		CRC32 crc32 = new CRC32();
		crc32.update(body);
		return crc32;
	}
	
	public static List<byte[]> encodeIndexNode(List<Entry> entryList, String compress) throws IOException {
		List<byte[]> ret = Collections.emptyList();
		for(Entry entry : entryList) {
			ret.add(encodeIndexNode(entry, compress));
		}
		return ret;
	}

	private static byte[] encodeIndexNode(Entry entry, String compress) throws IOException {
		Serializer serializer = SerializerFactory.getSerializer(entry);
		byte[] body = serializer.serialize(entry);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(BontenOutputStream bos = new BontenOutputStream(baos)) {
			bos.writeInt(body.length);
			bos.writeUnsignedInt(Utils.getCRCValue(body));
			bos.write(body);
			bos.writeEndTag();
			return baos.toByteArray();
		}
	}
}
