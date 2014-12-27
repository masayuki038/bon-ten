package net.wrap_trap.bonten;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BloomTest {

  @Test
  public void testBloom() {
    final Bloom bloom = new Bloom(2000);
    bloom.add("test".getBytes());
    assertThat(bloom.member("test".getBytes()), is(true));
    assertThat(bloom.member("test1".getBytes()), is(false));
  }
}
