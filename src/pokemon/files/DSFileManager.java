package pokemon.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pokemon.files.types.TypeEnum;
import pokemon.files.types.TypedArray;
import pokemon.files.types.TypedNumber;

/**
 * Class that contains static methods to read and write C-equivalent data types
 * in a file.
 */
public class DSFileManager {

	private DSFileManager() {

	}

	/**
	 * Reads a long from the stream that has the specified size.
	 * 
	 * @param type the type to read from the stream
	 * @return the long value read
	 * @throws IOException if a read problem occurs
	 */
	public static TypedNumber read(InputStream inStream, TypeEnum type) throws IOException {
		int byteSize = type.getByteSize();
		byte[] buffer = new byte[byteSize];
		int bytesRead = inStream.read(buffer);

		if (bytesRead != byteSize) {
			throw new IOException("Could not read %d bytes from the stream".formatted(byteSize));
		}

		long result = 0;
		for (int i = 0; i < byteSize; i++) {
			result = (result << 8) + (buffer[byteSize - (1 + i)] & 0xFF);
		}

		return new TypedNumber(result, type);
	}

	/**
	 * Reads n times the same type from the stream.
	 * 
	 * @return results from the stream
	 * @throws IOException
	 * @see #read(InputStream, TypeEnum)
	 */
	public static TypedArray read(InputStream inStream, TypeEnum type, int n) throws IOException {
		TypedNumber[] results = new TypedNumber[n];
		for (int i = 0; i < n; i++) {
			results[i] = read(inStream, type);
		}

		return new TypedArray(results, type);
	}

	/**
	 * Reads a value from the stream that corresponds to the specified number, and
	 * put it into that number.
	 * 
	 * @param number the number wrapper
	 * @throws IOException
	 * @see #read(InputStream, TypeEnum)
	 */
	public static void read(InputStream inStream, TypedNumber number) throws IOException {
		number.setValue(read(inStream, number.getType()).getValue());
	}

	/**
	 * Reads an array from the stream that corresponds to the specified type and
	 * length, and put it into that number.
	 * 
	 * @param array the array wrapper
	 * @throws IOException
	 * @see #read(InputStream, TypeEnum, int)
	 */
	public static void read(InputStream inStream, TypedArray array) throws IOException {
		array.setValues(read(inStream, array.getType(), array.length()).getValues());
	}

	/**
	 * Reads a string from the stream. An unconstrained string ALWAYS terminates
	 * with \0 (NULL) character.
	 * 
	 * @return the read string
	 * @throws IOException
	 * @see #read(TypeEnum)
	 */
	public static TypedArray readString(InputStream inStream) throws IOException {
		String result = "";
		char current;
		do {
			current = read(inStream, TypeEnum.CHAR).getCharValue();
			result += current;
		} while (current != 0); // Stop when included \0

		// Remove last char
		result = result.substring(0, result.length() - 1);

		return new TypedArray(result);
	}
	
	/**
	 * Reads bytes from the stream and returns the number of read bytes
	 * 
	 * @param data
	 * @param len
	 * @throws IOException if anything happens with the write
	 */
	public static int readBytes(InputStream inStream, byte[] data) throws IOException {
		return inStream.read(data);
	}

	/**
	 * Writes a value to the stream and format it according to the specified type.
	 * 
	 * @param value the value to write
	 * @param type  to format the value
	 * @throws IOException if anything happens with the write
	 */
	public static void write(OutputStream outStream, TypedNumber value) throws IOException {
		int byteSize = value.getType().getByteSize();
		byte[] buffer = new byte[byteSize];
		long valueToWrite = value.getValue();

		for (int i = 0; i < byteSize; i++) {
			// Little endian!
			buffer[i] = (byte) (valueToWrite & 0xFF);
			valueToWrite >>= 8;
		}

		outStream.write(buffer);
	}

	/**
	 * Writes multiple values of the same type to the stream
	 * 
	 * @param value the values to write
	 * @throws IOException if anything happens with the write
	 */
	public static void write(OutputStream outStream, TypedArray value) throws IOException {
		for (TypedNumber toWrite : value.getValues()) {
			write(outStream, toWrite);
		}
	}

	/**
	 * Writes a string to the stream and appends a \0 (NULL) if specified
	 * 
	 * @param value    the boolean to write
	 * @param endsNull true if a \0 (NULL) has to be append to the String
	 * @throws IOException if anything happens with the write
	 */
	public static void writeString(OutputStream outStream, TypedArray value, boolean endsNull) throws IOException {
		write(outStream, value);

		if (endsNull) {
			write(outStream, new TypedNumber(0, TypeEnum.CHAR));
		}
	}

	/**
	 * Writes bytes to the stream
	 * 
	 * @param data the bytes to write
	 * @param len the number of bytes to write
	 * @throws IOException if anything happens with the write
	 */
	public static void writeBytes(OutputStream outStream, byte[] data, int len) throws IOException {
		outStream.write(data, 0, len);
	}
	
	/**
	 * Writes bytes to the stream
	 * 
	 * @param data the bytes to write
	 * @throws IOException if anything happens with the write
	 */
	public static void writeBytes(OutputStream outStream, byte[] data) throws IOException {
		writeBytes(outStream, data, data.length);
	}

}
