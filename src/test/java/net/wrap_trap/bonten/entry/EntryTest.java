package net.wrap_trap.bonten.entry;

import static net.wrap_trap.bonten.Utils.toBytes;
import static org.hamcrest.core.Is.is;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class EntryTest {

  @Test
  public void testNotExpired() {
    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, 1);
    final Entry entry = new KeyValueEntry(toBytes("foo"), toBytes("bar"), cal.getTime());
    Assert.assertThat(entry.expired(), is(false));
  }

  @Test
  public void testExpired() {
    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, -1);
    final Entry entry = new KeyValueEntry(toBytes("foo"), toBytes("bar"), cal.getTime());
    Assert.assertThat(entry.expired(), is(true));
  }

  @Test
  public void testExpiredWhenTimestampNotSet() {
    final Entry entry = new KeyValueEntry(toBytes("foo"), toBytes("bar"));
    Assert.assertThat(entry.expired(), is(false));
  }

  @Test
  public void testPosLenEntryExpired() {
    final Entry entry = new PosLenEntry(toBytes("foo"), 1L, 1);
    Assert.assertThat(entry.expired(), is(false));
  }

  @Test
  public void testEstimateNodeSizeIncrementWithKeyValueEntry() {
    final Entry entry = new KeyValueEntry(toBytes("フー"), toBytes("バー"));
    Assert.assertThat(entry.estimateNodeSizeIncrement(), is(21));

    final Entry entry2 = new KeyValueEntry(toBytes("フー"), toBytes("バー"), new Date());
    Assert.assertThat(entry2.estimateNodeSizeIncrement(), is(21));
  }

  @Test
  public void testEstimateNodeSizeIncrementWithDeletedEntry() {
    final Entry entry = new DeletedEntry(toBytes("フー"));
    Assert.assertThat(entry.estimateNodeSizeIncrement(), is(22));

    final Entry entry2 = new DeletedEntry(toBytes("フー"), new Date());
    Assert.assertThat(entry2.estimateNodeSizeIncrement(), is(22));
  }

  @Test
  public void testEstimateNodeSizeIncrementWithPosLenEntry() {
    final Entry entry = new PosLenEntry(toBytes("フー"), 1L, 1);
    Assert.assertThat(entry.estimateNodeSizeIncrement(), is(15));
  }
}
