package net.wrap_trap.bonten.serializer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.util.Date;

import net.wrap_trap.bonten.deserializer.Deserializer;
import net.wrap_trap.bonten.deserializer.DeserializerFactory;
import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;

import org.junit.Assert;
import org.junit.Test;

public class DeletedEntrySerializeTest {

	@Test
	public void testSerializeDeletedEntry() throws IOException {
		DeletedEntry deletedEntry = new DeletedEntry("foo".getBytes());
		Serializer serializer = SerializerFactory.getSerializer(deletedEntry);
		byte[] bytes = serializer.serialize(deletedEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof DeletedEntry), is(true));
		DeletedEntry deserializedDeletedEntry = (DeletedEntry)entry;
		Assert.assertThat(deserializedDeletedEntry.getKey(), equalTo(deletedEntry.getKey()));
	}

	@Test
	public void testSerializeDeletedEntryWithTimestamp() throws IOException {
		DeletedEntry deletedEntry = new DeletedEntry("foo".getBytes(), new Date());
		Serializer serializer = SerializerFactory.getSerializer(deletedEntry);
		byte[] bytes = serializer.serialize(deletedEntry);
		
		Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
		Entry entry = deserializer.deserialize(bytes);
		
		Assert.assertThat((entry instanceof DeletedEntry), is(true));
		DeletedEntry deserializedDeletedEntry = (DeletedEntry)entry;
		Assert.assertThat(deserializedDeletedEntry.getKey(), equalTo(deletedEntry.getKey()));
		Assert.assertThat(deserializedDeletedEntry.getValue(), equalTo(deletedEntry.getValue()));		
		Assert.assertThat(deserializedDeletedEntry.getTimestamp(), equalTo(deletedEntry.getTimestamp()));		
	}
}
