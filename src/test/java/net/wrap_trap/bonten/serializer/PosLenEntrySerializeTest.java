package net.wrap_trap.bonten.serializer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import net.wrap_trap.bonten.deserializer.Deserializer;
import net.wrap_trap.bonten.deserializer.DeserializerFactory;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.PosLenEntry;

import org.junit.Assert;
import org.junit.Test;

public class PosLenEntrySerializeTest {

  @Test
  public void testSerializePosLenEntry() throws IOException {
    final long pos = Long.MAX_VALUE;
    final PosLenEntry posLenEntry = new PosLenEntry("foo".getBytes(), pos, Integer.MAX_VALUE);
    final Serializer serializer = SerializerFactory.getSerializer(posLenEntry);
    final byte[] bytes = serializer.serialize(posLenEntry);

    final Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
    final Entry entry = deserializer.deserialize(bytes);

    assertThat((entry instanceof PosLenEntry), is(true));
    final PosLenEntry deserializedPosLenEntry = (PosLenEntry) entry;
    assertThat(deserializedPosLenEntry.getKey(), equalTo(posLenEntry.getKey()));
    assertThat(deserializedPosLenEntry.getPos(), equalTo(posLenEntry.getPos()));
    assertThat(deserializedPosLenEntry.getLen(), equalTo(posLenEntry.getLen()));
  }
}
