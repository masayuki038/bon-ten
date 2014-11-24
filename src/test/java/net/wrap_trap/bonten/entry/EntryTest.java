package net.wrap_trap.bonten.entry;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static net.wrap_trap.bonten.Utils.toBytes;

public class EntryTest {
	
	@Test
	public void testNotExpired() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 1);
		Entry entry = new KeyValueEntry(toBytes("foo"), toBytes("bar"), cal.getTime());
		Assert.assertThat(entry.expired(), is(false));
	}
	
	@Test
	public void testExpired() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1);
		Entry entry = new KeyValueEntry(toBytes("foo"), toBytes("bar"), cal.getTime());
		Assert.assertThat(entry.expired(), is(true));
	}
	
	@Test
	public void testExpiredWhenTimestampNotSet() {
		Entry entry = new KeyValueEntry(toBytes("foo"), toBytes("bar"));
		Assert.assertThat(entry.expired(), is(false));
	}
	
	@Test
	public void testPosLenEntryExpired() {
		Entry entry = new PosLenEntry(toBytes("foo"), 1L, 1L);
		Assert.assertThat(entry.expired(), is(false));
	}
	
	@Test
	public void testEstimateNodeSizeIncrementWithKeyValueEntry() {
		Entry entry = new KeyValueEntry(toBytes("フー"), toBytes("バー"));
		Assert.assertThat(entry.estimateNodeSizeIncrement(), is(21));
		
		Entry entry2 = new KeyValueEntry(toBytes("フー"), toBytes("バー"), new Date());
		Assert.assertThat(entry2.estimateNodeSizeIncrement(), is(21));
	}
	
	@Test
	public void testEstimateNodeSizeIncrementWithDeletedEntry() {
		Entry entry = new DeletedEntry(toBytes("フー"));
		Assert.assertThat(entry.estimateNodeSizeIncrement(), is(22));
		
		Entry entry2 = new DeletedEntry(toBytes("フー"), new Date());
		Assert.assertThat(entry2.estimateNodeSizeIncrement(), is(22));
	}
	
	@Test
	public void testEstimateNodeSizeIncrementWithPosLenEntry() {
		Entry entry = new PosLenEntry(toBytes("フー"), 1L, 1L);
		Assert.assertThat(entry.estimateNodeSizeIncrement(), is(15));
	}
}
