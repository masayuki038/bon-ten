package net.wrap_trap.bonten.serializer;

import java.io.IOException;

import net.wrap_trap.bonten.entry.Entry;

public interface Serializer {

  byte[] serialize(Entry entry) throws IOException;
}
