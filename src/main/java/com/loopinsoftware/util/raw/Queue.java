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

import com.loopinsoftware.util.event.DataListener;

import java.util.ArrayList;

/**
 * This class encapsulates a raw byte queue for handling byte streaming. It stores data in a ring
 * queue but returns it in the natural order (from 0 to capacity) in an auxiliary byte array.
 *
 * @author Antonio Vicente Martin
 */
public class Queue {

    /**
     * The byte array to store all data
     */
    private byte[] rawRingData;
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
     * The initil size of the queue
     */
    private int initialSize;
    /**
     * The current size of the queue
     */
    private int size;
    /**
     * The list of listeners
     */
    private ArrayList<DataListener> dataListeners;

    /**
     * Creates a new RawQueue with a specified {@code capacity}
     *
     * @param capacity The capacity of the queue
     */
    public Queue(int capacity) {
        this(new byte[capacity]);
    }

    /**
     * Creates a new RawQueue with a specified bytes array
     *
     * @param data The byte array
     */
    public Queue(byte[] data) {
        this(data, 0, false);
    }

    /**
     * Creates a new RawQueue with a specified bytes array
     *
     * @param data The byte array
     * @param size The initial data size
     */
    public Queue(byte[] data, int size) {
        this(data, size, false);
    }

    /**
     * Creates a new RawQueue with a copy of the specified bytes array
     *
     * @param data The byte array
     * @param size The initial data size
     * @param copy Whether the array must be a copy or not
     */
    public Queue(byte[] data, int size, boolean copy) {
        setRawData(data, size, copy);
        this.dataListeners = new ArrayList<DataListener>();
    }

    /**
     * Pushes the {@code src} data into {@code dst}
     *
     * @param src     The byte array to read
     * @param dst     The byte array to push
     * @param dstSize The number of elements in dst
     * @return The number of remaining original bytes. A negative value means that those number of
     * {@code src} bytes are truncated.
     */
    public static int push(byte[] src, byte[] dst, int dstSize) {
        return push(src, 0, src.length, dst, dstSize);
    }

    /**
     * Pushes the {@code src} data into {@code dst}, with specified {@code from} and {@code to}
     * {@code src}'s indexes
     *
     * @param src     The byte array to read
     * @param srcFrom The start position to read
     * @param srcTo   The ending position to read
     * @param dst     The byte array to push
     * @param dstSize The number of elements in dst
     * @return The number of remaining original bytes. A negative value means that those number of
     * {@code src} bytes are truncated.
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

        // Truncate data: src{1,2,3,4,5,6,7,8,9,0} -push-> dst{3,2,1} =
        // dst{8,9,0}
        if (numberOfBytesToRead > dst.length) {
            numberOfBytesToRead = dst.length;
            srcFrom = srcTo - numberOfBytesToRead;
            System.arraycopy(src, srcFrom, dst, 0, numberOfBytesToRead);
            return remainingBytes;
        }

        // Compute the number of bytes to displace
        int numberOfBytesToDisplace = (dstSize + numberOfBytesToRead) - dst.length;

        // If a negative value is computed, it means it's not necessary to do a
        // displacement
        if (numberOfBytesToDisplace > 0) {
            // Move old values from ending to starting positions: |12345| =
            // |345--|
            System.arraycopy(dst, numberOfBytesToDisplace, dst, 0, dstSize - numberOfBytesToDisplace);
            dstSize -= numberOfBytesToDisplace;
        }

        // Push new data
        System.arraycopy(src, srcFrom, dst, dstSize, numberOfBytesToRead);

        return remainingBytes;
    }

    /**
     * Adds the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes, when
     * possible
     *
     * @param src The byte array to read
     */
    public void add(byte[] src) {
        add(src, 0, src.length);
    }

    /**
     * Adds the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes, when
     * possible
     *
     * @param src     The byte array to read
     * @param srcFrom The start position to read
     * @param srcTo   The ending position to read
     */
    public void add(byte[] src, int srcFrom, int srcTo) {
        insert(src, srcFrom, srcTo, false);
    }

    /**
     * Pushes the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes
     *
     * @param src     The byte array to read
     * @param srcFrom The start position to read
     * @param srcTo   The ending position to read
     */
    public void push(byte[] src, int srcFrom, int srcTo) {
        insert(src, srcFrom, srcTo, true);
    }

    /**
     * Insertes the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes
     *
     * @param src              The byte array to read
     * @param srcFrom          The start position to read
     * @param srcTo            The ending position to read
     * @param overwriteOldData Wether the old data is going to be overwritten
     */
    private void insert(byte[] src, int srcFrom, int srcTo, boolean overwriteOldData) {

        // The total number of bytes are going to be read
        int numberOfBytesToRead = srcTo - srcFrom;

        // Throw an exception in case of bad indexes
        if (numberOfBytesToRead < 1) {
            throw new IllegalArgumentException("Bad src indexes. to:" + srcTo + " must be greater than from:" + srcFrom);
        }

        boolean wasFull = isFull();

        // Ensure that no data is overwritten
        if (!overwriteOldData) {
            if (wasFull) {
                return;
            }
            int freeSpace = capacity - size;
            numberOfBytesToRead = Math.min(freeSpace, numberOfBytesToRead);
        }

        // Data overflow
        if (src.length > capacity) {
            push(src, srcFrom, srcTo, rawRingData, capacity);
            head = 0;
            tail = 0;
            size = capacity;
            if (!wasFull && isFull()) {
                fireOnFull();
            }
            return;
        }

        int lastData = Math.min(capacity - tail, numberOfBytesToRead);
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

        if (!wasFull && isFull()) {
            fireOnFull();
        }

    }

    /**
     * Pushes the {@code src} with specified {@code from} and {@code to} {@code src}'s indexes
     *
     * @param src The byte array to read
     */
    public void push(byte[] src) {
        push(src, 0, src.length);
    }

    /**
     * Looks a the first bytes from the queue
     *
     * @param dst The output buffer to store looked data
     * @return The number of bytes looked
     */
    public int peek(byte[] dst) {
        return peek(dst, dst.length);
    }

    /**
     * Looks a the first bytes from the queue
     *
     * @param dst              The output buffer to store looked data
     * @param numberOfElements The number of elements to peek
     * @return The number of bytes looked
     */
    public int peek(byte[] dst, int numberOfElements) {
        return peek(dst, numberOfElements, 0);
    }

    /**
     * Looks a the first bytes from the queue
     *
     * @param dst              The output buffer to store looked data
     * @param numberOfElements The number of elements to peek
     * @param dstOffset        Initial index to write
     * @return The number of bytes looked
     */
    public int peek(byte[] dst, int numberOfElements, int dstOffset) {
        if (isEmpty()) {
            return 0;
        }

        if (numberOfElements < 0) {
            throw new ArrayIndexOutOfBoundsException(numberOfElements);
        }

        if (dstOffset < 0) {
            throw new ArrayIndexOutOfBoundsException(dstOffset);
        }

        if (numberOfElements + dstOffset > dst.length) {
            throw new ArrayIndexOutOfBoundsException(numberOfElements + dstOffset);
        }

        numberOfElements = Math.min(numberOfElements, size);

        int lastData = capacity - head;
        int endIndex = (head + numberOfElements) % capacity;

        // If ringed, a split copy is needed
        if (head >= endIndex) {
            System.arraycopy(rawRingData, head, dst, dstOffset, lastData);
            System.arraycopy(rawRingData, 0, dst, lastData, endIndex);
        } else {
            System.arraycopy(rawRingData, head, dst, dstOffset, numberOfElements);
        }

        return numberOfElements;
    }

    /**
     * Removes the first bytes from the queue
     *
     * @param dst              The output buffer to store removed data
     * @param numberOfElements The number of elements to pop
     * @return The number of bytes removed
     */
    public int pop(byte[] dst, int numberOfElements) {
        return pop(dst, numberOfElements, 0);
    }

    /**
     * Removes the first bytes from the queue
     *
     * @param dst              The output buffer to store removed data
     * @param numberOfElements The number of elements to pop
     * @param dstOffset        The destination index offset
     * @return The number of bytes removed
     */
    public int pop(byte[] dst, int numberOfElements, int dstOffset) {
        numberOfElements = peek(dst, numberOfElements, dstOffset);

        boolean wasEmpty = isEmpty();
        size -= numberOfElements;
        head = (head + numberOfElements) % capacity;

        if (!wasEmpty && isEmpty()) {
            fireOnEmpty();
        }

        return numberOfElements;
    }

    /**
     * Removes the first bytes from the queue
     *
     * @param dst The output buffer to store removed data
     * @return The number of bytes removed
     */
    public int pop(byte[] dst) {
        return pop(dst, dst.length);
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
     */
    public void getRawData(byte[] data) {
        peek(data);
    }

    /**
     * @param data The raw byte data to set
     * @param size The size of the data to set
     * @param copy Whether the array must be a copy or not
     */
    public void setRawData(byte[] data, int size, boolean copy) {
        // Throw an exception if size doesn't match
        if (size > data.length) {
            throw new ArrayIndexOutOfBoundsException("size:" + size + " can't be greater than capacity:" + data.length);
        }

        this.capacity = data.length;
        this.initialSize = this.size = size;
        this.tail = size;

        if (copy) {
            // Make a copy of the original
            this.rawRingData = new byte[data.length];
            System.arraycopy(data, 0, this.rawRingData, 0, data.length);
        } else {
            // Reference the argument object
            this.rawRingData = data;
        }
        // this.rawData = new byte[data.length];
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

    /**
     * Adds a new dataListener to the listener list
     *
     * @param dataListener The DataListener to add
     * @return true (as specified by Collection.add)
     */
    public boolean addDataListener(DataListener dataListener) {
        return dataListeners.add(dataListener);
    }

    /**
     * Removes a dataListener from the listener list
     *
     * @param dataListener The DataListener to remove
     * @return true if the element is removed, false otherwise
     */
    public boolean removeDataListener(DataListener dataListener) {
        return dataListeners.remove(dataListener);
    }

    /**
     * Fires the onFull() method for each dataListener
     */
    private void fireOnFull() {
        for (int i = 0; i < dataListeners.size(); i++) {
            dataListeners.get(i).onFull();
        }
    }

    /**
     * Fires the onEmpty() method for each dataListener
     */
    private void fireOnEmpty() {
        for (int i = 0; i < dataListeners.size(); i++) {
            dataListeners.get(i).onEmpty();
        }
    }

    /**
     * Resets the intials bounds values, when the Queue was created
     */
    public void reset() {
        this.size = this.initialSize;
        this.tail = size;
        this.head = 0;
    }
}
