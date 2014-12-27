package net.wrap_trap.bonten.serializer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.math.BigInteger;

import net.wrap_trap.bonten.deserializer.Deserializer;
import net.wrap_trap.bonten.deserializer.DeserializerFactory;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.PosLenEntry;

import org.junit.Assert;
import org.junit.Test;

public class PosLenEntrySerializeTest {

  @Test
  public void testSerializePosLenEntry() throws IOException {
    final BigInteger pos = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1L));
    final PosLenEntry posLenEntry = new PosLenEntry("foo".getBytes(), pos, Integer.MAX_VALUE + 1L);
    final Serializer serializer = SerializerFactory.getSerializer(posLenEntry);
    final byte[] bytes = serializer.serialize(posLenEntry);

    final Deserializer deserializer = DeserializerFactory.getDeserializer(bytes[0]);
    final Entry entry = deserializer.deserialize(bytes);

    Assert.assertThat((entry instanceof PosLenEntry), is(true));
    final PosLenEntry deserializedPosLenEntry = (PosLenEntry) entry;
    Assert.assertThat(deserializedPosLenEntry.getKey(), equalTo(posLenEntry.getKey()));
    Assert.assertThat(deserializedPosLenEntry.getPos(), equalTo(posLenEntry.getPos()));
    Assert.assertThat(deserializedPosLenEntry.getLen(), equalTo(posLenEntry.getLen()));
  }
}
