package net.wrap_trap.bonten;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class UtilsTest {

	@Test
	public void testCompareBytesAGraterThanB() {
		byte[] a = {10, 20};
		byte[] b = {10, 19};
		Assert.assertThat((Utils.compareBytes(a, b) > 0), is(true));
	}
	
	@Test
	public void testCompareBytesAGraterThanB2() {
		byte[] a = {10, 20};
		byte[] b = {10};
		Assert.assertThat((Utils.compareBytes(a, b) > 0), is(true));
	}

	@Test
	public void testCompareBytesAGraterThanB3() {
		byte[] a = {10};
		byte[] b = {9, 10};
		Assert.assertThat((Utils.compareBytes(a, b) > 0), is(true));
	}
	
	@Test
	public void test() {
	}

}
