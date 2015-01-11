package net.wrap_trap.bonten;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

public class ReaderTest {

  @Test
  public void testSerializeBloom() throws IOException, ClassNotFoundException {
    final Bloom bloom = new Bloom(2000);
    Reader reader = new Reader("dummy");
    Writer writer = new Writer("dummy");
    
    Bloom bloom2 = reader.deserializeBloom(writer.serializeBloom(bloom));
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
}
