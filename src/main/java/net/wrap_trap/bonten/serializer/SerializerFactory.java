package net.wrap_trap.bonten.serializer;

import java.util.HashMap;
import java.util.Map;

import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;

public class SerializerFactory {
	
	private static Map<Class<?>, Serializer> map;
	
	static {
		map = new HashMap<>();
		map.put(KeyValueEntry.class, new KeyValueEntrySerializer());
		map.put(DeletedEntry.class, new DeletedEntrySerializer());
	}
	
	public static Serializer getSerializer(Entry entry) {
		Serializer serializer = map.get(entry.getClass());
		if(serializer == null) {
			throw new IllegalArgumentException("Invalid entry class: " + entry.getClass().getName());
		}
		return serializer;
	}
}
