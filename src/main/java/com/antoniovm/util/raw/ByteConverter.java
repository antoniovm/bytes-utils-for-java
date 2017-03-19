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
package com.antoniovm.util.raw;

/**
 * @author Antonio Vicente Martin
 * 
 *         This class is a tool to convert or split primitives types into bytes
 *         arrays
 */
public class ByteConverter {

	/**
	 * Returns the corresponding byte inside the value
	 * 
	 * @param value
	 *            The assembled value
	 * @param position
	 *            The byte index
	 * @return The byte value at the specified index
	 */
	public static byte getByteAt(long value, int position) {
		byte byteSelected = (byte) (value >> 8 * position);
		return byteSelected;
	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The short value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	public static void toBytesArray(short value, byte[] bytes, boolean littleEndian) {
		toBytesArray(value, bytes, 0, littleEndian);
	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The int value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	public static void toBytesArray(int value, byte[] bytes, boolean littleEndian) {
		toBytesArray(value, bytes, 0, littleEndian);
	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The long value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	public static void toBytesArray(long value, byte[] bytes, boolean littleEndian) {
		toBytesArray(value, bytes, 0, littleEndian);
	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The long value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param start
	 *            The start iteration index
	 * @param valueLength
	 *            The number of bytes of input value
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	private static void toBytesArray(long value, byte[] bytes, int start, int valueLength, boolean littleEndian) {
		int i = littleEndian ? valueLength - 1 : 0;
		int sum = littleEndian ? -1 : 1;
		int end = start + valueLength;

		if (end > bytes.length) {
			throw new ArrayIndexOutOfBoundsException("array length: " + bytes.length + ", index: " + end);
		}

		for (int j = start; j < end && i < valueLength && i >= 0; i += sum) {
			bytes[j++] = getByteAt(value, i);
		}

	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The long value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param start
	 *            The start iteration index
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	public static void toBytesArray(long value, byte[] bytes, int start, boolean littleEndian) {
		toBytesArray(value, bytes, start, 8, littleEndian);
	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The int value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param start
	 *            The start iteration index
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	public static void toBytesArray(int value, byte[] bytes, int start, boolean littleEndian) {
		toBytesArray(value, bytes, start, 4, littleEndian);
	}

	/**
	 * Returns a value from a primitive splitted into a specified byte array
	 * 
	 * @param value
	 *            The short value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param start
	 *            The start iteration index
	 * @param littleEndian
	 *            The array of bytes endianess
	 */
	public static void toBytesArray(short value, byte[] bytes, int start, boolean littleEndian) {
		toBytesArray(value, bytes, start, 2, littleEndian);
	}

	/**
	 * Return an integer built from an array of bytes
	 * 
	 * @param bytes
	 *            The raw bytes
	 * @param index
	 *            The initial position
	 * @param littleEndian
	 *            The byte order
	 * @return The int value
	 */
	public static int toIntValue(byte[] bytes, int index, boolean littleEndian) {
		return (int) toValue(bytes, index, 4, littleEndian);
	}

	/**
	 * Return an integer built from an array of bytes
	 * 
	 * @param bytes
	 *            The raw bytes
	 * @param index
	 *            The initial position
	 * @param bytesPerValue
	 *            The number of bytes per value
	 * @param littleEndian
	 *            The byte order
	 * @return The int value
	 */
	public static long toValue(byte[] bytes, int index, int bytesPerValue, boolean littleEndian) {
		int value = 0;

		int i = 0;
		int increment = 1;

		// The index + bytesPerValue int bytes must be within array bounds
		if (index + bytesPerValue > bytes.length) {
			throw new ArrayIndexOutOfBoundsException("At least " + bytesPerValue + " bytes needed.");
		}

		// The last bound byte index
		int lastByteIndex = index + bytesPerValue - 1;

		// This is the most significative byte in big endian
		int mostSignificativeByte = bytes[lastByteIndex];

		if (littleEndian) {
			// Reverse iteration
			increment = -1;

			// Skip the MSB (-2 = - (lastBoundIndex + MSB))
			i = bytesPerValue - 2;
			mostSignificativeByte = bytes[index++];
			lastByteIndex++;
		}

		// Preserve sign
		value += mostSignificativeByte << 8 * (bytesPerValue - 1);

		for (int j = index; j < lastByteIndex && i < bytesPerValue && i >= 0; i += increment) {
			// The bit 'and' operation is used to get an unsigned value
			value += (bytes[j++] & 0xFF) << 8 * i;
			// value += bytes[j++] << 8 * i;
		}

		return value;

	}

	/**
	 * Copies and converts the bytes array into a doubles array
	 * 
	 * @param src
	 *            The array to read
	 * @param fromSrc
	 *            The low index in src
	 * @param sampleSize
	 *            The size of the sample
	 * @param dst
	 *            The destination array
	 * @param fromDst
	 *            The low index in src
	 * @param toDst
	 *            The high index in src
	 * @param normalize
	 *            Write double data represented in values between 1.0 and 0.0
	 * @param littleEndian
	 *            Whether the src array is encoded in little endian or not
	 */
	public static void toDoublesArray(byte[] src, int fromSrc, int sampleSize, double[] dst, int fromDst, int toDst,
			boolean normalize, boolean littleEndian) {
		double normalizationCoefficient = normalize ? Math.pow(256, sampleSize) : 1.0;

		for (int i = fromDst; i < toDst; i++) {
			dst[i] = toValue(src, fromSrc + i * sampleSize, sampleSize, littleEndian) / normalizationCoefficient;
		}

	}

}
