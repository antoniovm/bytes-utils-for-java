/**
 * 
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
	 * @param position
	 * @return
	 */
	public static byte getByteAt(long value, int position) {
		byte byteSelected = (byte) (value >> 8 * position);
		return byteSelected;
	}

	/**
	 * Returns an int value splitted into a littleEndian byte array
	 * 
	 * @param value
	 *            The int value
	 * @return bytes The value's bytes
	 */
	public static byte[] toBytesArray(int value) {
		return toBytesArray(value, 4, true);
	}

	/**
	 * Returns an int value splitted into a byte array
	 * 
	 * @param value
	 *            The int value
	 * @param littleEndian
	 *            The array of bytes endianess
	 * @return bytes The value's bytes
	 */
	public static byte[] toBytesArray(int value, boolean littleEndian) {
		return toBytesArray(value, 4, littleEndian);
	}

	/**
	 * Returns a new array of bytes with the splitted value
	 * 
	 * @param value
	 *            The vale to convert
	 * @param numBytes
	 *            The number of bytes
	 * @param littleEndian
	 *            The array of bytes endianess
	 * @return bytes The array of bytes
	 */
	public static byte[] toBytesArray(long value, int numBytes, boolean littleEndian) {
		byte[] bytes = new byte[numBytes];

		return toBytesArray(value, bytes, littleEndian);
	}

	/**
	 * Returns a value splitted into a specified byte array
	 * 
	 * @param value
	 *            The long value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param littleEndian
	 *            The array of bytes endianess
	 * @return bytes The value's bytes
	 */
	public static byte[] toBytesArray(long value, byte[] bytes, boolean littleEndian) {
		return toBytesArray(value, bytes, 0, bytes.length, littleEndian);
	}

	/**
	 * Returns a value splitted into a specified byte array
	 * 
	 * @param value
	 *            The long value to split
	 * @param bytes
	 *            The array of bytes to write in
	 * @param start
	 *            The start iteration index
	 * @param end
	 *            The end iterarion index
	 * @param littleEndian
	 *            The array of bytes endianess
	 * @return bytes The value's bytes
	 */
	public static byte[] toBytesArray(long value, byte[] bytes, int start, int end, boolean littleEndian) {
		int i = start;
		int sum = 1;

		if (littleEndian) {
			// Reverse iteration
			i = end - 1;
			sum = -1;
		}

		for (int j = start; j < bytes.length && i < end && i >= start; i += sum) {
			bytes[j++] = getByteAt(value, i);
		}

		return bytes;
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
		int sum = 1;

		// The index + bytesPerValue int bytes must be within array bounds
		if (index + bytesPerValue > bytes.length) {
			throw new IllegalArgumentException("At least " + bytesPerValue + " bytes needed.");
		}

		if (littleEndian) {
			// Reverse iteration
			i = bytesPerValue - 1;
			sum = -1;
		}

		for (int j = index; j < bytes.length && i < bytesPerValue && i >= 0; i += sum) {
			// The bit and operation is used to get an unsigned value
			// value += (bytes[j++] & 0xFF) << 8 * i;
			value += bytes[j++] << 8 * i;
		}

		return value;

	}

	/**
	 * Copies and converts the bytes array into a doubles array
	 * 
	 * @param from
	 * @param startFrom
	 * @param sampleSize
	 * @param to
	 * @param startTo
	 * @param endTo
	 * @param normalize
	 * @param littleEndian
	 */
	public static void toDoublesArray(byte[] from, int startFrom, int sampleSize, double[] to, int startTo, int endTo,
			boolean normalize, boolean littleEndian) {
		double normalizationCoefficient = normalize ? Math.pow(256, sampleSize) : 1.0;

		for (int i = startTo; i < endTo; i++) {
			to[i] = toValue(from, startFrom + i * sampleSize, sampleSize, littleEndian) / normalizationCoefficient;
		}

	}

}
