/**
 * 
 */
package com.antoniovm.util.raw;

import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author Antonio Vicente Martin
 *
 */
public class QueueTest {

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testNewQueueArrayIndexOutOfBoundsException() {
		byte[] test = { 1, 2, 0, 0, 0 };
		new Queue(test, 6);
	}

	@Test
	public void testPushNoDisplacement() {
		byte[] test = { 1, 2, 0, 0, 0 };
		Queue qTest = new Queue(test, 2);
		byte[] newData = { 3, 4 };
		byte[] expected = { 1, 2, 3, 4, 0 };

		qTest.push(newData);

		Assert.assertArrayEquals(expected, qTest.getRawData());
	}

	@Test
	public void testPushNoTruncate() {
		byte[] test = { 1, 2, 3, 4, 5 };
		Queue qTest = new Queue(test, test.length, true);
		byte[] newData = { 6, 7 };
		byte[] expected = { 3, 4, 5, 6, 7 };

		qTest.push(newData);

		Assert.assertArrayEquals(expected, qTest.getRawData());
	}

	@Test
	public void testPushTruncate() {
		byte[] test = { 1, 2, 3 };
		byte[] newData = { 6, 7, 8, 9 };
		byte[] expected = { 7, 8, 9 };

		Queue.push(newData, test, test.length);

		Assert.assertArrayEquals(expected, test);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPushBadIndexes() {
		byte[] test = { 1, 2, 3 };
		byte[] newData = {};

		Queue.push(newData, test, test.length);

	}

}
