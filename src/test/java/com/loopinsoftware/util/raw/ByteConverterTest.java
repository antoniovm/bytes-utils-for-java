/*
 * Copyright (C) 2014 Loopin Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loopinsoftware.util.raw;

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

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testToByteArrayArrayIndexOutOfBoundException() {
		int test = 0x04030201;
		byte[] expected = { 1, 2, 3, 4 };
		byte[] out = new byte[expected.length];

		ByteConverter.toBytesArray(test, out, 1, false);

		Assert.assertArrayEquals(expected, out);
	}

	@Test
	public void testToByteArrayShortBigEndian() {
		short test1 = 0x0201;
		short test2 = 0x0403;
		byte[] expected = { 1, 2, 3, 4, 0 };
		byte[] out = new byte[expected.length];

		ByteConverter.toBytesArray(test1, out, false);
		ByteConverter.toBytesArray(test2, out, 2, false);

		Assert.assertArrayEquals(expected, out);
	}

	@Test
	public void testToByteArrayIntBigEndian() {
		int test1 = 0x04030201;
		int test2 = 0x08070605;
		byte[] expected = { 1, 2, 3, 4, 5, 6, 7, 8 };
		byte[] out = new byte[expected.length];

		ByteConverter.toBytesArray(test1, out, 0, false);
		ByteConverter.toBytesArray(test2, out, 4, false);

		Assert.assertArrayEquals(expected, out);
	}

	@Test
	public void testToByteArrayLongBigEndian() {
		long test1 = 0x0706050403020100l;
		long test2 = 0x0f0e0d0c0b0a0908l;
		byte[] expected = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0 };
		byte[] out = new byte[expected.length];

		ByteConverter.toBytesArray(test1, out, false);
		ByteConverter.toBytesArray(test2, out, 8, false);

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
