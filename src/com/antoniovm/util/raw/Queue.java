/**
 * 
 */
package com.antoniovm.util.raw;


/**
 * This class encapsulates a raw byte queue for handling byte streaming
 * 
 * @author Antonio Vicente Martin
 *
 */
public class Queue {

	/**
	 * The byte array to store all data
	 */
	private byte[] data;

	/**
	 * The number of elements inside the array
	 */
	private int size;

	/**
	 * Creates a new RawQueue with a specified {@code capacity}
	 * 
	 * @param capacity
	 *            The capacity of the queue
	 */
	public Queue(int capacity) {
		this(new byte[capacity]);
	}

	/**
	 * Creates a new RawQueue with a specified bytes array
	 * 
	 * @param data
	 *            The byte array
	 */
	public Queue(byte[] data) {
		this(data, 0, false);
	}

	/**
	 * Creates a new RawQueue with a specified bytes array
	 * 
	 * @param data
	 *            The byte array
	 * @param size
	 *            The initial data size
	 */
	public Queue(byte[] data, int size) {
		this(data, size, false);
	}

	/**
	 * Creates a new RawQueue with a copy of the specified bytes array
	 * 
	 * @param data
	 *            The byte array
	 * @param size
	 *            The initial data size
	 * @param copy
	 *            Whether the array is a copy or not
	 */
	public Queue(byte[] data, int size, boolean copy) {
		// Throw an exception if size doesn't match
		if (size > data.length) {
			throw new ArrayIndexOutOfBoundsException("size:" + size + " can't be greater than capacity:" + data.length);
		}

		this.size = size;

		if (copy) {
			// Make a copy of the original
			this.data = new byte[data.length];
			System.arraycopy(data, 0, this.data, 0, data.length);
		} else {
			// Reference the argument object
			this.data = data;
		}
	}

	/**
	 * Pushes the {@code src} data into {@code dst}
	 * 
	 * @param src
	 *            The byte array to read
	 * @param dst
	 *            The byte array to push
	 * @param dstSize
	 *            The number of elements in dst
	 * @return The number of remaining original bytes. A negative value means that those number of {@code src} bytes are
	 *         truncated.
	 */
	public static int push(byte[] src, byte[] dst, int dstSize) {
		return push(src, 0, src.length, dst, dstSize);
	}

	/**
	 * Pushes the {@code src} data into {@code dst}, with specified {@code from} and {@code to} {@code src}'s indexes
	 * 
	 * @param src
	 *            The byte array to read
	 * @param srcFrom
	 *            The start position to read
	 * @param srcTo
	 *            The ending position to read
	 * @param dst
	 *            The byte array to push
	 * @param dstSize
	 *            The number of elements in dst
	 * @return The number of remaining original bytes. A negative value means that those number of {@code src} bytes are
	 *         truncated.
	 */
	public static int push(byte[] src, int srcFrom, int srcTo, byte[] dst, int dstSize) {

		// The total number of bytes are going to be read
		int numberOfBytesToRead = srcTo - srcFrom;

		// Throw an exception in case of bad indexes
		if (numberOfBytesToRead < 1) {
			throw new IllegalArgumentException("Bad src indexes. to:" + srcTo + " must be greater than from:" + srcFrom);
		}

		// Remaining bytes
		int remainingBytes = dst.length - numberOfBytesToRead;

		// Truncate data: src{1,2,3,4,5,6,7,8,9,0} -push-> dst{3,2,1} = dst{8,9,0}
		if (numberOfBytesToRead > dst.length) {
			numberOfBytesToRead = dst.length;
			srcFrom = srcTo - numberOfBytesToRead;
			System.arraycopy(src, srcFrom, dst, 0, numberOfBytesToRead);
			return remainingBytes;
		}

		// Compute the number of bytes to displace
		int numberOfBytesToDisplace = (dstSize + numberOfBytesToRead) - dst.length;

		// If a negative value is computed, it means it's not necessary to do a displacement
		if (numberOfBytesToDisplace > 0) {
			// Move old values from ending to starting positions: |12345| = |345--|
			System.arraycopy(dst, numberOfBytesToDisplace, dst, 0, dstSize);
			dstSize -= numberOfBytesToDisplace;
		}

		// Push new data
		System.arraycopy(dst, dstSize, src, srcFrom, numberOfBytesToRead);

		return remainingBytes;
	}

	/**
	 * Pushes the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes
	 * 
	 * @param src
	 *            The byte array to read
	 * @param srcFrom
	 *            The start position to read
	 * @param srcTo
	 *            The ending position to read
	 * @return The number of remaining original bytes. A negative value means that those number of {@code src} bytes are
	 *         truncated.
	 */
	public int push(byte[] src, int srcFrom, int srcTo) {
		int remainingBytes = push(src, srcFrom, srcTo, data, size);

		// Calculate the new size
		int totalNewBytes = (srcTo - srcFrom);
		
		if ((totalNewBytes + size)>data.length) {
			// Data overflow
			size = data.length;
		}else {
			// Increment size
			size += totalNewBytes;
		}
		
		return remainingBytes;
	}

	/**
	 * Pushes the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes
	 * 
	 * @param src
	 *            The byte array to read
	 * @return The number of remaining original bytes. A negative value means that those number of {@code src} bytes are
	 *         truncated.
	 */
	public int push(byte[] src) {
		return push(src, 0, src.length);
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * 
	 * @param data
	 *            The raw byte data to set
	 * @param size
	 *            The size of the data to set
	 */
	public void setData(byte[] data, int size) {
		this.data = data;
		this.size = size;
	}

	/**
	 * @param size
	 *            The size of the data to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
}
