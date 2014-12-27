package net.wrap_trap.bonten.serializer;

import java.util.HashMap;
import java.util.Map;

import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;
import net.wrap_trap.bonten.entry.PosLenEntry;

public class SerializerFactory {

  private static Map<Class<?>, Serializer> map;

  static {
    map = new HashMap<>();
    map.put(KeyValueEntry.class, new KeyValueEntrySerializer());
    map.put(DeletedEntry.class, new DeletedEntrySerializer());
    map.put(PosLenEntry.class, new PosLenEntrySerializer());
  }

  public static Serializer getSerializer(final Entry entry) {
    final Serializer serializer = map.get(entry.getClass());
    if (serializer == null)
      throw new IllegalArgumentException("Invalid entry class: " + entry.getClass().getName());
    return serializer;
  }
}
