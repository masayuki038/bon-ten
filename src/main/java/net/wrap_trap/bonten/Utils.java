package net.wrap_trap.bonten;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.CRC32;

import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.serializer.Serializer;
import net.wrap_trap.bonten.serializer.SerializerFactory;

import com.google.common.primitives.UnsignedBytes;

public class Utils {

  public static void ensureExpiry() {
    final BontenConfig config = Bonten.getConfig();
    final int expirySecs = config.getInt("expiry_secs");
    if (expirySecs < 0)
      throw new IllegalArgumentException(String.format("Invalid 'expiry_secs': %d", expirySecs));
  }

  public static double log2(final double e) {
    return Math.log(e) / Math.log(2);
  }

  public static int compareBytes(final byte[] a, final byte[] b) {
    return UnsignedBytes.lexicographicalComparator().compare(a, b);
  }

  public static byte[] toBytes(final String str) {
    return str.getBytes(Charset.forName("UTF-8"));
  }

  public static boolean checkCRC(final long crc, final byte[] body) {
    final CRC32 crc32 = getCRC(body);
    return (crc == crc32.getValue());
  }

  public static long getCRCValue(final byte[] body) {
    final CRC32 crc32 = getCRC(body);
    return crc32.getValue();
  }

  private static CRC32 getCRC(final byte[] body) {
    final CRC32 crc32 = new CRC32();
    crc32.update(body);
    return crc32;
  }

  public static byte[] encodeIndexNode(final List<Entry> entryList, final String compress) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      for (final Entry entry : entryList) {
        baos.write(encodeIndexNode(entry, compress));
      }
      return baos.toByteArray();
    }
  }

  private static byte[] encodeIndexNode(final Entry entry, final String compress) throws IOException {
    final Serializer serializer = SerializerFactory.getSerializer(entry);
    final byte[] body = serializer.serialize(entry);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (BontenOutputStream bos = new BontenOutputStream(baos)) {
      bos.writeInt(body.length);
      bos.writeUnsignedInt(Utils.getCRCValue(body));
      bos.write(body);
      bos.writeEndTag();
      return baos.toByteArray();
    }
  }
}
