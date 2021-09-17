package stavwpz.data.sss;

/**
 * A functional interface for declaring format types in {@link SSStorage SSS} 
 * 
 * @author Stav c:
 */
@FunctionalInterface
public interface SSSFormatType {

	/**
	 * The conversion method to an SSS format.
	 * @param values The array of values to convert
	 * @return The converted string.
	 */
	public abstract String format(String[] values);
	
}
