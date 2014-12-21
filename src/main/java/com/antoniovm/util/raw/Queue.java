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
	private byte[] rawRingData;
	/**
	 * The byte array to return all data sorted
	 */
	private byte[] rawData;

	/**
	 * The index to the first element
	 */
	private int head;
	/**
	 * The index to the last element + 1
	 */
	private int tail;
	/**
	 * The capacity of the queue
	 */
	private int capacity;
	/**
	 * The current size of the queue
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
		this(data, data.length, false);
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
	 *            Whether the array must be a copy or not
	 */
	public Queue(byte[] data, int size, boolean copy) {
		setRawData(data, size, copy);
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
			System.arraycopy(dst, numberOfBytesToDisplace, dst, 0, dstSize - numberOfBytesToDisplace);
			dstSize -= numberOfBytesToDisplace;
		}

		// Push new data
		System.arraycopy(src, srcFrom, dst, dstSize, numberOfBytesToRead);

		return remainingBytes;
	}

	/**
	 * Pushes the {@code src} with specified {@code from} and {@code to}
	 * {@code src}'s indexes
	 * 
	 * @param src
	 *            The byte array to read
	 * @param srcFrom
	 *            The start position to read
	 * @param srcTo
	 *            The ending position to read.
	 */
	public void push(byte[] src, int srcFrom, int srcTo) {

		// The total number of bytes are going to be read
		int numberOfBytesToRead = srcTo - srcFrom;

		// Throw an exception in case of bad indexes
		if (numberOfBytesToRead < 1) {
			throw new IllegalArgumentException("Bad src indexes. to:" + srcTo
					+ " must be greater than from:" + srcFrom);
		}

		// Data overflow
		if (src.length > capacity) {
			push(src, srcFrom, srcTo, rawRingData, capacity);
			head = 0;
			tail = capacity;
		}

		int lastData = capacity - tail;
		int freeSpace = capacity - size;

		// If the right bound is reached, a src split is needed
		if (lastData < numberOfBytesToRead) {
			System.arraycopy(src, srcFrom, rawRingData, tail, lastData);
			srcFrom += lastData;
			tail = (tail + lastData) % capacity;
			lastData = srcTo - lastData;
		}

		System.arraycopy(src, srcFrom, rawRingData, tail, lastData);
		tail = (tail + lastData) % capacity;

		// Check if head movement is needed
		if (freeSpace < numberOfBytesToRead) {
			int headOffset = numberOfBytesToRead - freeSpace;
			head = (head + headOffset) % capacity;
		}

		size = Math.min(capacity, size + numberOfBytesToRead);

	}

	/**
	 * Pushes the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes
	 * 
	 * @param src
	 *            The byte array to read
	 */
	public void push(byte[] src) {
		push(src, 0, src.length);
	}

	/**
	 * Looks a the first bytes from the queue
	 * 
	 * @param dst
	 *            The output buffer to store looked data
	 * @param numberOfElements
	 *            The number of elements to peek
	 * @return The number of bytes looked
	 */
	public int peek(byte[] dst, int numberOfElements) {
		if (isEmpty()) {
			return 0;
		}

		numberOfElements = Math.min(numberOfElements, capacity);

		int lastData = capacity - head;
		int endIndex = (head + numberOfElements) % capacity;

		// If ringed, a split copy is needed
		if (head >= endIndex) {
			System.arraycopy(rawRingData, head, dst, 0, lastData);
			System.arraycopy(rawRingData, 0, dst, lastData, endIndex);
		} else {
			System.arraycopy(rawRingData, head, dst, 0, numberOfElements);
		}

		return numberOfElements;
	}

	/**
	 * Removes the first bytes from the queue
	 * 
	 * @param dst
	 *            The output buffer to store removed data
	 * @param numberOfElements
	 *            The number of elements to pop
	 * @return The number of bytes removed
	 */
	public int pop(byte[] dst, int numberOfElements) {
		numberOfElements = peek(dst, numberOfElements);

		size -= numberOfElements;
		head = (head + numberOfElements) % capacity;
	
		return numberOfElements;
	}

	/**
	 * Removes the first bytes from the queue
	 * 
	 * @param dst
	 *            The output buffer to store removed data
	 * @return The number of bytes removed
	 */
	public int pop(byte[] dst) {
		return pop(dst, dst.length);
	}

	/**
	 * Copy all data in a new array in natural order
	 */
	private void setNaturalOrder() {
		peek(rawData, size);
	}

	/**
	 * Restores the initial values for the indexes
	 */
	public void clear() {
		this.head = 0;
		this.tail = 0;
		this.size = 0;
	}

	/**
	 * Returns the raw data
	 * 
	 * @return the data
	 */
	public byte[] getRawData() {
		setNaturalOrder();
		return rawData;
	}

	/**
	 * 
	 * @param data
	 *            The raw byte data to set
	 * @param size
	 *            The size of the data to set
	 * @param copy
	 *            Whether the array must be a copy or not
	 */
	public void setRawData(byte[] data, int size, boolean copy) {
		// Throw an exception if size doesn't match
		if (size > data.length) {
			throw new ArrayIndexOutOfBoundsException("size:" + size
					+ " can't be greater than capacity:" + data.length);
		}

		this.capacity = data.length;
		this.size = size;
		this.tail = size;

		if (copy) {
			// Make a copy of the original
			this.rawRingData = new byte[data.length];
			System.arraycopy(data, 0, this.rawRingData, 0, data.length);
		} else {
			// Reference the argument object
			this.rawRingData = data;
		}
		this.rawData = new byte[data.length];
	}

	/**
	 * Return the size of the queue
	 * 
	 * @return The size of the queue
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Return the capacity of the queue
	 * 
	 * @return The capacity of the queue
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the capacity of the queue
	 * 
	 * @param capacity
	 *            The capacity of the queue
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Checks if the queue is empty
	 * 
	 * @return true if is empty, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Checks if the queue is full
	 * 
	 * @return true if is full, false otherwise
	 */
	public boolean isFull() {
		return size == capacity;
	}
}

