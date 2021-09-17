package stavwpz.discord.classmate;

/**
 * An interface to run code on main code. In order for this to work, this class needs to be hooked to {@link Main#runners}
 * @author Stav c:
 */
public interface IMainRunner {
	/**
	 * The implementation of {@link IMainRunner} to run code on main code.<br>NOTE: This code runs before JDA is initialized
	 */
	void runOnMain();
}
