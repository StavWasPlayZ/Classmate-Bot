package stavwpz.data.sss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * A class for managing the custom file type *.sss for storing preferences and data.<br>
 * Basically I was too lazy to pick up on Json.<br><br>
 * 
 * SSS is a random language I invented used to store key-value-pairs.
 * @author Stav c:
 */
public final class SSStorage {

	private static final HashMap<String, SSSFormatType> CONVERSION_TYPES = createDefaultTypes();
	private static HashMap<String, SSSFormatType> createDefaultTypes() {
		final HashMap<String, SSSFormatType> map = new HashMap<String, SSSFormatType>();
		map.putAll(Map.of(
			"id", values -> values[0].replaceAll("[^0-9]", ""),
			
			"array", values -> {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < values.length; i++)
					builder.append(values[i]+(((i+1) == values.length) ? "" : ","));
				return builder.toString();
			},
			
			"boolean", values -> (values[0].equals("true") || values[0].equals("false")) ? values[0] : null,
			
			"string", values -> arrayToString(values, true)
		));
		return map;
	}
	public static void addConversionTypes(Map<String, SSSFormatType> types) {
		CONVERSION_TYPES.putAll(types);
	}

	
	public String value, description;
	public SSStorage(String value) {
		this.value = value;
	}
	public SSStorage(String value, String description) {
		this.value = value;
		this.description = description;
	}
	@Override
	public String toString() {
		return value+((description != null) ? " ("+description+")" : "");
	}
	
	/**
	 * Converts human-readable values to the SSS format. 
	 * @param value The array of values to convert.<br>Uses the whole array only with specific types, I.E <i>array</i>.
	 * @param type The type of conversion
	 * @return The formatted string.
	 */
	public static String formatValue(String[] value, String type) {
		if (value == null || value.length == 0 || !CONVERSION_TYPES.containsKey(type))
			return null;
		
		return CONVERSION_TYPES.get(type).format(value);
	}
	
	
	/**
	 * Gets a specified value in <code>key</code> using <code>scanner</code>.<br><br>
	 * <b>NOTE:</b> <code>scanner</code> is used to read the file type and is asked as a parameter since there are
	 * many ways to read a file type. It's heavily recommended that you'd create a raw {@link Scanner} object as you're passing
	 * the parameters.<br>
	 * This method also deals with closing the stream.
	 * @param scanner The scanner (that usually is linked to a file)
	 * @param key The key to get from
	 * @return The value of <code>key</code>
	 */
	public static SSStorage get(Scanner scanner, String key) {
		while (scanner.hasNextLine()) {
			final String[] values = scanner.nextLine().split("\\s+");
			if (values[0].equals(key)) {
				scanner.close();
				return getStorageObject(values);
			}
		}
		scanner.close();
		return null;
	}
	/**
	 * Gets the raw value from <code>key</code> using <code>scanner</code><br><br>
	 * <b>NOTE:</b> <code>scanner</code> is used to read the file type and is asked as a parameter since there are
	 * many ways to read a file type. It's heavily recommended that you'd create a raw {@link Scanner} object as you're passing
	 * the parameters.<br>
	 * This method also deals with closing the stream.
	 * @param scanner The scanner (that usually is linked to a file)
	 * @param key The key to get from
	 * @return The raw value of <code>key</code>
	 */
	public static String getRaw(Scanner scanner, String key) {
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			int firstSpaceI = line.indexOf(" ");
			if (line.startsWith(key+" ")) {
				scanner.close();
				return line.substring(firstSpaceI, line.length());
			}
		}
		scanner.close();
		return null;
	}
	/**
	 * Gets all values inside of the <code>scanner</code> stream.<br><br>
	 * <b>NOTE:</b> <code>scanner</code> is used to read the file type and is asked as a parameter since there are
	 * many ways to read a file type. It's heavily recommended that you'd create a raw {@link Scanner} object as you're passing
	 * the parameters.<br>
	 * This method also deals with closing the stream.
	 * @param scanner The scanner (that usually is linked to a file)
	 * @return all values in <code>scanner</code> as a {@link HashMap}
	 */
	public static HashMap<String, SSStorage> getAll(Scanner scanner) {
		HashMap<String, SSStorage> result = new HashMap<String, SSStorage>();
		while (scanner.hasNextLine()) {
			String[] values = scanner.nextLine().split("\\s+");
			result.put(values[0], getStorageObject(values));
		}
		scanner.close();
		return result;
	}
	private static SSStorage getStorageObject(String[] values) {
		return (values.length == 2) ? new SSStorage(values[1]) : new SSStorage(values[1], arrayToString(Arrays.copyOfRange(values, 2, values.length), false));
	}
	
	/**
	 * Writes a <code>value</code> attached to <code>key</code> in a <code>file</code> in SSS.<br>
	 * If <code>value</code> already exists, it will be overridden.
	 * @param key The key leading to <code>value</code>
	 * @param value The value to be attached to <code>key</code>
	 * @param file The file to write on
	 * @param skipIfNull Should the writer not write or delete the provided key if value is null?
	 * @throws IOException In case of a missing file or a writing issue
	 */
	public static void write(String key, String value, File file) throws IOException {
		Scanner scanner = new Scanner(file);
		StringBuilder builder = new StringBuilder();
		String newLine = key+" "+value;
		
		boolean did = false;
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			
			if (!did && (line.split("\\s+")[0]).equals(key)) {
				did = true;
				builder.append((value == null) ? (scanner.hasNextLine() ? scanner.nextLine() : "") : newLine);
			} else
				builder.append(line);
			
			builder.append("\n");
		}
		scanner.close();
		if (!did && value != null)
			builder.append(newLine);
		
		FileWriter writer = new FileWriter(file);
		writer.write(builder.toString().trim());
		writer.close();
	}
	/**
	 * Writes a <code>value</code> attached to <code>key</code> in a <code>file</code> with the SSS formatting
	 * associated with the conversion delegate specified by <code>conversionType</code>.<br>
	 * If value already exists, it will be overridden.
	 * @param key The key leading to <code>value</code>
	 * @param values The array of values to convert and write.
	 * 		Uses the whole array only if <code><i>conversionType</i></code> is set to "<i>array</i>".
	 * @param conversionType The type of conversion
	 * @param file The file to write on
	 * @throws IOException In case of a missing file or a writing issue
	 */
	public static void writeFormatted(String key, String[] values, String conversionType, File file) throws IOException {
		write(key, formatValue(values, conversionType), file);
	}
	
	private static String arrayToString(String[] array, boolean replaceWhitespace) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; i++)
			builder.append(array[i]+(((i+1) == array.length) ? "" : (replaceWhitespace ? "U+0020" : " ")));
		return builder.toString();
	}
	
}
