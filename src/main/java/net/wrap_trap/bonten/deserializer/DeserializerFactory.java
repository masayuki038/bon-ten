package net.wrap_trap.bonten.deserializer;

import static net.wrap_trap.bonten.Bonten.TAG_DELETED;
import static net.wrap_trap.bonten.Bonten.TAG_DELETED2;
import static net.wrap_trap.bonten.Bonten.TAG_KV_DATA;
import static net.wrap_trap.bonten.Bonten.TAG_KV_DATA2;
import static net.wrap_trap.bonten.Bonten.TAG_POSLEN;

import java.util.HashMap;
import java.util.Map;

public class DeserializerFactory {

  private static Map<Byte, Deserializer> map;

  static {
    map = new HashMap<>();
    map.put(TAG_KV_DATA, new KeyValueEntryDeserializer(false));
    map.put(TAG_DELETED, new DeletedEntryDeserializer(false));
    map.put(TAG_KV_DATA2, new KeyValueEntryDeserializer(true));
    map.put(TAG_DELETED2, new DeletedEntryDeserializer(true));
    map.put(TAG_POSLEN, new PosLenEntryDeserializer());
  }

  public static Deserializer getDeserializer(final byte tag) {
    final Deserializer deserializer = map.get(tag);
    if (deserializer == null)
      throw new IllegalArgumentException("Invalid tag: " + tag);
    return deserializer;
  }
}
