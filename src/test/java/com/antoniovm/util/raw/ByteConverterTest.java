/**
 * 
 */
package com.antoniovm.util.raw;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Antonio Vicente Martin
 * 
 */
public class ByteConverterTest {

	@Test
	public void testGetByteAt0() {
		int test = 0x04030201;
		Assert.assertEquals(1, ByteConverter.getByteAt(test, 0));
	}

	@Test
	public void testGetByteAt1() {
		int test = 0x04030201;
		Assert.assertEquals(2, ByteConverter.getByteAt(test, 1));
	}

	@Test
	public void testGetByteAt2() {
		int test = 0x04030201;
		Assert.assertEquals(3, ByteConverter.getByteAt(test, 2));
	}

	@Test
	public void testGetByteAt3() {
		int test = 0x04030201;
		Assert.assertEquals(4, ByteConverter.getByteAt(test, 3));
	}

	@Test
	public void testToByteArrayLittleEndian() {
		int test = 0x04030201;
		byte[] expected = { 4, 3, 2, 1 };
		byte[] out = new byte[expected.length];

		ByteConverter.toBytesArray(test, out, true);

		Assert.assertArrayEquals(expected, out);
	}

	@Test
	public void testToByteArrayBigEndian() {
		int test = 0x04030201;
		byte[] expected = { 1, 2, 3, 4 };
		byte[] out = new byte[expected.length];

		ByteConverter.toBytesArray(test, out, false);

		Assert.assertArrayEquals(expected, out);
	}

	@Test
	public void testToIntValueLittleEndian() {
		byte[] test = { 4, 3, 2, 1 };
		int expected = 0x04030201;
		Assert.assertEquals(expected, ByteConverter.toIntValue(test, 0, true));
	}

	@Test
	public void testToIntValueBigEndian() {
		int expected = 0x04030201;
		byte[] test = { 1, 2, 3, 4 };
		Assert.assertEquals(expected, ByteConverter.toIntValue(test, 0, false));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testToValueArrayIndexOutOfBoundsException() {
		byte[] test = { 4, 3, 2 };
		ByteConverter.toValue(test, 0, 4, true);
	}

	@Test
	public void testToDoublesArrayNoNormalizeBigEndian() {
		byte[] test = { 1, 0, 2, 0 };
		double[] expected = { 1.0, 2.0 };
		double[] out = new double[2];
		
		ByteConverter.toDoublesArray(test, 0, 2, out, 0, out.length, false, false);
		
		Assert.assertArrayEquals(expected, out, 0);
	}

	@Test
	public void testToDoublesArrayNormalizeBigEndian() {
		byte[] test = { 64, 0, 32, 0 };
		double[] expected = { 0.0009765625, 0.00048828125 };
		double[] out = new double[expected.length];

		ByteConverter.toDoublesArray(test, 0, 2, out, 0, out.length, true, false);

		Assert.assertArrayEquals(expected, out, 0);
	}

}
