package net.wrap_trap.bonten;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import net.wrap_trap.bonten.entry.KeyValueEntry;

public class WriterTest {

	private Writer writer;
	
	@Before
	public void setUp() throws IOException {
		Bonten.init();
		writer = new Writer("./test.dat");
		writer.open(null);
	}
	
	@Test
	public void testToWriteSingleKeyValueEntry() throws IOException {
		writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar")));
	}
	
	@Test
	public void testToWriteMultipleKeyValueEntry() throws IOException {
		writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar")));
		writer.add(new KeyValueEntry(Utils.toBytes("hoge"), Utils.toBytes("hogehoge")));
	}

	@Test
	public void testToWriteMultipleKeyValueEntryByIllegalOrder() throws IOException {
		try {
			writer.add(new KeyValueEntry(Utils.toBytes("hoge"), Utils.toBytes("hogehoge")));
			writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar")));
			fail();
		} catch(IllegalStateException e) {}
	}

	@Test
	public void testToHugeKeyValueEntry() throws IOException {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 8192; i ++) {
			sb.append("b");
		}
		writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes(sb.toString())));
	}
}
