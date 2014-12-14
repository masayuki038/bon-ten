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
		KeyValueEntry kvEntry2 = (KeyValueEntry)entry;
		Assert.assertThat(kvEntry.getKey(), equalTo(kvEntry2.getKey()));
		Assert.assertThat(kvEntry.getValue(), equalTo(kvEntry2.getValue()));		
	}
	
	@Test
	public void testSerializeKeyValueEntryWithTimestamp() throws IOException {
		KeyValueEntry kvEntry = new KeyValueEntry("foo".getBytes(), "bar".getBytes(), new Date());
		Serializer serializer = SerializerFactory.getSerializer(kvEntry);
		byte[] bytes = serializer.serialize(kvEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof KeyValueEntry), is(true));
		KeyValueEntry kvEntry2 = (KeyValueEntry)entry;
		Assert.assertThat(kvEntry.getKey(), equalTo(kvEntry2.getKey()));
		Assert.assertThat(kvEntry.getValue(), equalTo(kvEntry2.getValue()));		
		Assert.assertThat(kvEntry.getTimestamp(), equalTo(kvEntry2.getTimestamp()));		
	}
	
	@Test
	public void testSerializeDeletedEntry() throws IOException {
		DeletedEntry deletedEntry = new DeletedEntry("foo".getBytes());
		Serializer serializer = SerializerFactory.getSerializer(deletedEntry);
		byte[] bytes = serializer.serialize(deletedEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof DeletedEntry), is(true));
		DeletedEntry deletedEntry2 = (DeletedEntry)entry;
		Assert.assertThat(deletedEntry.getKey(), equalTo(deletedEntry2.getKey()));
	}

	@Test
	public void testSerializeDeletedEntryWithTimestamp() throws IOException {
		DeletedEntry deletedEntry = new DeletedEntry("foo".getBytes(), new Date());
		Serializer serializer = SerializerFactory.getSerializer(deletedEntry);
		byte[] bytes = serializer.serialize(deletedEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof DeletedEntry), is(true));
		DeletedEntry deletedEntry2 = (DeletedEntry)entry;
		Assert.assertThat(deletedEntry.getKey(), equalTo(deletedEntry2.getKey()));
		Assert.assertThat(deletedEntry.getValue(), equalTo(deletedEntry2.getValue()));		
		Assert.assertThat(deletedEntry.getTimestamp(), equalTo(deletedEntry2.getTimestamp()));		
	}
	
	@Test
	public void testSerializePosLenEntry() throws IOException {
		BigInteger pos = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1L));
		PosLenEntry posLenEntry = new PosLenEntry("foo".getBytes(), pos, Integer.MAX_VALUE + 1L);
		Serializer serializer = SerializerFactory.getSerializer(posLenEntry);
		byte[] bytes = serializer.serialize(posLenEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof PosLenEntry), is(true));
		PosLenEntry posLenEntry2 = (PosLenEntry)entry;
		Assert.assertThat(posLenEntry.getKey(), equalTo(posLenEntry2.getKey()));
		Assert.assertThat(posLenEntry.getPos(), equalTo(posLenEntry2.getPos()));
		Assert.assertThat(posLenEntry.getLen(), equalTo(posLenEntry2.getLen()));
	}
}
