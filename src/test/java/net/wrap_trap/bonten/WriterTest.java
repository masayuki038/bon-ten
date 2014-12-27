package net.wrap_trap.bonten;

import static org.junit.Assert.fail;

import java.io.IOException;

import net.wrap_trap.bonten.entry.KeyValueEntry;

import org.junit.Before;
import org.junit.Test;

public class WriterTest {

  private Writer writer;

  @Before
  public void setUp() throws IOException {
    Bonten.init();
    this.writer = new Writer("./test.dat");
    this.writer.open(null);
  }

  @Test
  public void testToWriteSingleKeyValueEntry() throws IOException {
    this.writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar")));
  }

  @Test
  public void testToWriteMultipleKeyValueEntry() throws IOException {
    this.writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar")));
    this.writer.add(new KeyValueEntry(Utils.toBytes("hoge"), Utils.toBytes("hogehoge")));
  }

  @Test
  public void testToWriteMultipleKeyValueEntryByIllegalOrder() throws IOException {
    try {
      this.writer.add(new KeyValueEntry(Utils.toBytes("hoge"), Utils.toBytes("hogehoge")));
      this.writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar")));
      fail();
    } catch (final IllegalStateException e) {}
  }

  @Test
  public void testToHugeKeyValueEntry() throws IOException {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 8192; i++) {
      sb.append("b");
    }
    this.writer.add(new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes(sb.toString())));
  }
}
