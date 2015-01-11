package net.wrap_trap.bonten;

import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

  @Test
  public void testCompareBytesAGraterThanB() {
    final byte[] a = { 10, 20 };
    final byte[] b = { 10, 19 };
    Assert.assertThat((Utils.compareBytes(a, b) > 0), is(true));
  }

  @Test
  public void testCompareBytesAGraterThanB2() {
    final byte[] a = { 10, 20 };
    final byte[] b = { 10 };
    Assert.assertThat((Utils.compareBytes(a, b) > 0), is(true));
  }

  @Test
  public void testCompareBytesAGraterThanB3() {
    final byte[] a = { 10 };
    final byte[] b = { 9, 10 };
    Assert.assertThat((Utils.compareBytes(a, b) > 0), is(true));
  }
}
