package net.wrap_trap.bonten;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;

import org.junit.Test;

public class ReaderTest {

  @Test
  public void testSerializeBloom() throws IOException, ClassNotFoundException {
    final Bloom bloom = new Bloom(2000);
    Reader reader = new Reader("dummy");
    
    Bloom bloom2 = reader.deserializeBloom(Writer.serializeBloom(bloom));
    assertThat(bloom2.n, is(bloom.n));
    assertThat(bloom2.mb, is(bloom.mb));
    assertThat(bloom2.e, is(bloom.e));
  } 
  
  @Test
  public void testSerializeBloomWithState() throws ClassNotFoundException, IOException {
    final Bloom bloom = new Bloom(2000);
    Reader reader = new Reader("dummy");
    Writer writer = new Writer("dummy");

    bloom.add("test".getBytes());
    assertThat(bloom.member("test".getBytes()), is(true));
    assertThat(bloom.member("test1".getBytes()), is(false));
    
    Bloom bloom2 = reader.deserializeBloom(writer.serializeBloom(bloom));
    assertThat(bloom2.n, is(bloom.n));
    assertThat(bloom2.mb, is(bloom.mb));
    assertThat(bloom2.e, is(bloom.e));
    
    assertThat(bloom2.member("test".getBytes()), is(true));
    assertThat(bloom2.member("test1".getBytes()), is(false));
  }
  
  @Test
  public void testGetFirstNode() throws IOException {
    Bonten.init();
    
    int level = 0;
    Reader reader = new Reader("dummy");
    Writer writer = new Writer("dummy");
    writer.open(new HashMap<>());
    KeyValueEntry newEntry = new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar"));
    writer.appendNode(level, newEntry);
    writer.close();
    
    reader.open(Read.RANDOM);
    List<Entry> entryList = reader.getFirstNode();
    assertThat(entryList.size(), is(1));
    Entry readEntry = entryList.get(0);
    assertThat(readEntry instanceof KeyValueEntry, is(true));
    assertThat(Utils.toString(((KeyValueEntry)readEntry).getKey()), is(Utils.toString(newEntry.getKey())));
    assertThat(Utils.toString(((KeyValueEntry)readEntry).getValue()), is(Utils.toString(newEntry.getValue())));    
  }
  
  @Test
  public void testGetFirstNodeWithTwoEntries() throws IOException {
    Bonten.init();
    
    int level = 0;
    Reader reader = new Reader("dummy");
    Writer writer = new Writer("dummy");
    writer.open(new HashMap<>());
    KeyValueEntry newEntry1 = new KeyValueEntry(Utils.toBytes("foo"), Utils.toBytes("bar"));
    KeyValueEntry newEntry2 = new KeyValueEntry(Utils.toBytes("hoge"), Utils.toBytes("hogehoge"));
    writer.appendNode(level, newEntry1);
    writer.appendNode(level, newEntry2);    
    writer.close();
    
    reader.open(Read.RANDOM);
    List<Entry> entryList = reader.getFirstNode();
    assertThat(entryList.size(), is(2));
    Entry readEntry1 = entryList.get(0);
    Entry readEntry2 = entryList.get(1);
    assertThat(readEntry1 instanceof KeyValueEntry, is(true));
    assertThat(readEntry2 instanceof KeyValueEntry, is(true));
    assertThat(Utils.toString(((KeyValueEntry)readEntry1).getKey()), is(Utils.toString(newEntry1.getKey())));
    assertThat(Utils.toString(((KeyValueEntry)readEntry1).getValue()), is(Utils.toString(newEntry1.getValue())));    
    assertThat(Utils.toString(((KeyValueEntry)readEntry2).getKey()), is(Utils.toString(newEntry2.getKey())));
    assertThat(Utils.toString(((KeyValueEntry)readEntry2).getValue()), is(Utils.toString(newEntry2.getValue())));    
  }
}
