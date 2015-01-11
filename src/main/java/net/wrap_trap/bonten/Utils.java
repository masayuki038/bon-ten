package net.wrap_trap.bonten;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;

import static org.msgpack.template.Templates.tList;
import static org.msgpack.template.Templates.TByteArray;
import net.wrap_trap.bonten.deserializer.Deserializer;
import net.wrap_trap.bonten.deserializer.DeserializerFactory;
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
  
  public static String toString(final byte[] bytes) {
    return new String(bytes, Charset.forName("UTF-8"));
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

  public static byte[] encodeIndexNodes(final List<Entry> entryList, final Compress compress) throws IOException {
      List<byte[]> encoded = entryList.stream()
        .map((entry) -> encodeIndexNode(entry, compress)).collect(Collectors.toList());
      encoded.add(0, new byte[]{(byte)0xFF});
      byte[] serialized = serializeEncodedEntryList(encoded);
      return compress.compress(serialized);
  }
  
  public static List<Entry> decodeIndexNodes(byte[] packed) throws IOException {
    byte[] serialized = Compress.uncompress(packed);
    List<byte[]> encodedList = deserializeToEncodedEntryList(serialized);
    byte[] endTag = encodedList.get(0);
    if(endTag.length != 1 || endTag[0] != (byte)0xFF) {
      throw new IllegalStateException("Invalid endTag");
    }
    return encodedList.subList(1, encodedList.size())
      .stream()
      .map((encodedEntry) -> decodedIndexNode(encodedEntry))
      .collect(Collectors.toList());
  }

  private static List<byte[]> deserializeToEncodedEntryList(byte[] serialized) throws IOException {
    try(ByteArrayInputStream bais = new ByteArrayInputStream(serialized)) {
      Template<List<byte[]>> listTmpl = tList(TByteArray);
      MessagePack msgpack = new MessagePack();
      Unpacker unpacker = msgpack.createUnpacker(bais);
      return unpacker.read(listTmpl);
    }
  }
  

  private static byte[] serializeEncodedEntryList(List<byte[]> encoded) throws IOException {
    try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      MessagePack msgpack = new MessagePack();
      Packer packer = msgpack.createPacker(baos);
      packer.write(encoded);
      return baos.toByteArray();
    }
  }

  private static byte[] encodeIndexNode(final Entry entry, final Compress compress) {
    final Serializer serializer = SerializerFactory.getSerializer(entry);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (BontenOutputStream bos = new BontenOutputStream(baos)) {
      final byte[] body = serializer.serialize(entry);
      bos.writeInt(body.length);
      bos.writeLong(Utils.getCRCValue(body));
      bos.write(body);
      bos.writeEndTag();
      return baos.toByteArray();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static Entry decodedIndexNode(byte[] encodedEntry) {
    final ByteArrayInputStream bais = new ByteArrayInputStream(encodedEntry);
    try(BontenInputStream bis = new BontenInputStream(bais)) {
      int bodyLength = bis.readInt();
      long crc = bis.readLong();
      byte[] body = new byte[bodyLength];
      bis.read(body);
      bis.readEndTag();
      
      if(!checkCRC(crc, body)) {
        throw new IllegalStateException("Invalid CRC: " + crc);
      }
          
      final Deserializer deserializer = DeserializerFactory.getDeserializer(body[0]);
      return deserializer.deserialize(body);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
