package net.wrap_trap.bonten.serializer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.primitives.UnsignedLong;

import net.wrap_trap.bonten.deserializer.Deserializer;
import net.wrap_trap.bonten.deserializer.DeserializerFactory;
import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;
import net.wrap_trap.bonten.entry.PosLenEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class KeyValueEntrySerializeTest {

	@Test
	public void testSerializeKeyValueEntry() throws IOException {
		KeyValueEntry kvEntry = new KeyValueEntry("foo".getBytes(), "bar".getBytes());
		Serializer serializer = SerializerFactory.getSerializer(kvEntry);
		byte[] bytes = serializer.serialize(kvEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof KeyValueEntry), is(true));
		KeyValueEntry deserializedKvEntry = (KeyValueEntry)entry;
		Assert.assertThat(deserializedKvEntry.getKey(), equalTo(kvEntry.getKey()));
		Assert.assertThat(deserializedKvEntry.getValue(), equalTo(kvEntry.getValue()));		
	}
	
	@Test
	public void testSerializeKeyValueEntryWithTimestamp() throws IOException {
		KeyValueEntry kvEntry = new KeyValueEntry("foo".getBytes(), "bar".getBytes(), new Date());
		Serializer serializer = SerializerFactory.getSerializer(kvEntry);
		byte[] bytes = serializer.serialize(kvEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof KeyValueEntry), is(true));
		KeyValueEntry deserializedKvEntry = (KeyValueEntry)entry;
		Assert.assertThat(deserializedKvEntry.getKey(), equalTo(kvEntry.getKey()));
		Assert.assertThat(deserializedKvEntry.getValue(), equalTo(kvEntry.getValue()));		
		Assert.assertThat(deserializedKvEntry.getTimestamp(), equalTo(kvEntry.getTimestamp()));		
	}
}
