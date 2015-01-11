package net.wrap_trap.bonten.serializer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Date;

import net.wrap_trap.bonten.deserializer.Deserializer;
import net.wrap_trap.bonten.deserializer.DeserializerFactory;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;

import org.junit.Assert;
import org.junit.Test;

public class KeyValueEntrySerializeTest {

  @Test
  public void testSerializeKeyValueEntry() throws IOException {
    final KeyValueEntry kvEntry = new KeyValueEntry("foo".getBytes(), "bar".getBytes());
    final Serializer serializer = SerializerFactory.getSerializer(kvEntry);
    final byte[] bytes = serializer.serialize(kvEntry);

    final Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
    final Entry entry = deserializer.deserialize(bytes);

    assertThat((entry instanceof KeyValueEntry), is(true));
    final KeyValueEntry deserializedKvEntry = (KeyValueEntry) entry;
    assertThat(deserializedKvEntry.getKey(), equalTo(kvEntry.getKey()));
    assertThat(deserializedKvEntry.getValue(), equalTo(kvEntry.getValue()));
  }

  @Test
  public void testSerializeKeyValueEntryWithTimestamp() throws IOException {
    final KeyValueEntry kvEntry = new KeyValueEntry("foo".getBytes(), "bar".getBytes(), new Date());
    final Serializer serializer = SerializerFactory.getSerializer(kvEntry);
    final byte[] bytes = serializer.serialize(kvEntry);

    final Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
    final Entry entry = deserializer.deserialize(bytes);

    assertThat((entry instanceof KeyValueEntry), is(true));
    final KeyValueEntry deserializedKvEntry = (KeyValueEntry) entry;
    assertThat(deserializedKvEntry.getKey(), equalTo(kvEntry.getKey()));
    assertThat(deserializedKvEntry.getValue(), equalTo(kvEntry.getValue()));
    assertThat(deserializedKvEntry.getTimestamp(), equalTo(kvEntry.getTimestamp()));
  }
}
