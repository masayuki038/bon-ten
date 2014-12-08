package net.wrap_trap.bonten.deserializer;

import java.io.IOException;

import net.wrap_trap.bonten.entry.Entry;

public interface Deserializer {
	
	Entry deserialize(byte[] body) throws IOException;
}
