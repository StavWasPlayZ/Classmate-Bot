package stavwpz.discord;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import stavwpz.data.sss.SSStorage;
import stavwpz.discord.classmate.commandManager.CommandManager;

/**
 * Some general utilities for this bot.
 * 
 * @author Stav c:
 */
public abstract class Utils {

	/**
	 * Sends an error message using {@link makeErrorEmbed}.<br>
	 * Basically a shorthand for
	 * 	<code><i>channel</i>.sendMessageEmbeds(makeErrorEmbed(<i>message</i>, <i>helpCmd</i>).build()).queue()</code>
	 * @param channel The channel to send the error message to
	 * @param message The message associated with this error
	 * @param helpCmd The command to execute to find more information about this command, optional
	 */
	public static void sendErrorEmbed(TextChannel channel, String message, String helpCmd) {
		channel.sendMessageEmbeds(makeErrorEmbed(message, helpCmd).build()).queue();
	}
	/**
	 * Makes a new, generic error {@link EmbedBuilder} with the following settings: 
	 * @param message The message associated with this error
	 * @param helpCmd The command to execute to find more information about this command, optional
	 * @return A new modifiable EmbedBuilder with the settings above.
	 */
	public static EmbedBuilder makeErrorEmbed(String message, String helpCmd) {
		//TODO replace '$' with guild prefix
		return new EmbedBuilder().setTitle("❌ שגיאה!").setColor(Color.RED).addField(message, "השתמש ב- `"+"$"+(helpCmd == null ? "sethelp" : helpCmd)+"` לרשימת המפתחות ותבנית הפקודה.", false);
	}
	
	/**
	 * Creates a new embed with the help format.<br>
	 * This method works best with {@link HashMap HashMaps}.
	 * @param title The title of the embed
	 * @param subtext The title of the field
	 * @param keys The set of your list to display. If you've used a HashMap then you could pass in {@link HashMap#keySet}
	 * @param checkMethod A method that iterates over the provided <code>keys</code>.
	 * 	Return there your value to display on the help list.<br>
	 * 	Note: you could use {@link getFormattedEntry} in there to get a template of that.
	 * @param footer Text to appear at the very bottom of the embed, optional
	 * @param absoluteFooter A footer that always appears at the bottom
	 * @return A new {@link EmbedBuilder} with the help format included.
	 */
	public static EmbedBuilder helpEmbedGenerator(String title, String subtext, Set<String> keys, MessageBuilder checkMethod, String footer, String absoluteFooter) {
		final StringBuilder builder = new StringBuilder();
		for (String key : keys)
			builder.append(checkMethod.check(key));
		builder.append("\n"+((footer == null) ? "" : footer+"\n\n")+"**__הערה:__** בשביל לעשות רווח בטקסט, צריך להחליף את הרווח ב- **U+0020**. רווח רגיל ייחשב כארגומנט אחר."+((absoluteFooter == null) ? "" : "\n\n"+absoluteFooter));
		
		return new EmbedBuilder().setTitle(title).addField(subtext, builder.toString(), false).setColor(Color.BLUE);
	}
	@FunctionalInterface
	public static interface MessageBuilder {
		String check(String key);
	}
	
	private static final String PARENT_FOLDER = "data";
	/**
	 * Gets a value by <code>key</code> inside of an {@link SSStorage SSS} file in path <code>path</code>.<br>
	 * In case of key not being found in path, <code>defPath</code> will be used instead.<br>
	 * It will also be used to merge with path if <code>conversionType</code> is <i>"array"</i> and <code>mergeWithDef</code> is true.
	 * @param conversionType What type is this key? (You can get this from the original file in most cases)
	 * @param path The path to the wanted SSS file
	 * @param guild The guild to get the value from
	 * @param defPath A backup path in case <code>path</code> doesn't contain the specified <code>key</code>.
	 * @param key The key attached to the value
	 * @return The requested value presented by a string
	 */
	public static String getValue(String path, Guild guild, String defPath, String key) {
		SSStorage value = null;
		try {
			value = SSStorage.get(new Scanner(new File(getGuildPath(guild, path))), key);
		} catch (FileNotFoundException e) {}
		if (value != null)
			return value.value;
		
		SSStorage defValue = SSStorage.get(new Scanner(getResourceAsStream(getDataPath(defPath))), key);
		return (defValue == null) ? null : defValue.value;
	}
	public static String getGuildPath(Guild guild, String dataLoctaion) {
		return PARENT_FOLDER+"/"+guild.getId()+"/"+dataLoctaion;
	}
	public static String getDataPath(String dataLocation) {
		return PARENT_FOLDER+"/"+dataLocation;
	}
	public static InputStream getResourceAsStream(String path) {
		return Utils.class.getClassLoader().getResourceAsStream(path);
	}
	public static File getGuildSaveFile(Guild guild, String dataPath) {
		final File guildSettings = new File(Utils.getGuildPath(guild, dataPath));
		guildSettings.getParentFile().mkdirs();
		try {
			guildSettings.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return guildSettings;
	}
	/**
	 * Gets a string representing an entry inside of a {@link HashMap HashMap}&#60;{@link String}, {@link SSStorage}&#62;.
	 * @param keyName The key of the wanted item
	 * @param value The value of the key
	 * @param markEmoji An emoji, or anything basically, to be at the start of the list item, sort of acting like a bullet
	 * @param includeValue Whether the value of <code>value</code> should be persent.
	 * @return A string representing an entry
	 */
	public static String getFormattedEntry(String keyName, SSStorage value, String markEmoji, boolean includeValue) {
		return String.format(markEmoji+" `%s`%s%s\n", keyName, (includeValue ? " `["+value.value+"]`" : ""), (value.description == null) ? "" : ": "+value.description);
	}
	public static String getFooter(String commandName, String arrayCmdName) {
		return String.format(("**שימוש הפקודה**: `{pref}%s`\n"+((arrayCmdName == null) ? "" : "למפתחות מסוג **`array`** ניתן ואף רצוי להשתמש ב- `{pref}%s`\n")+"\n*חוסר תוכן יוביל לשחזור המפתח*").replace("{pref}", "$"), commandName+" "+CommandManager.getCommand(commandName).arguments, arrayCmdName+" "+CommandManager.getCommand(arrayCmdName).arguments);
	}
	
	/**
	 * Writes or adds a value to a key provided by the <code>args</code> in the {@link SSStorage SSS Format}.
	 * @param originalFileValues The values of the formatter SSS file. You can get it using {@link SSStorage#getAll(Scanner)}, or the safer {@link getFileMap} version.
	 * @param args The arguments provided by the Discord user. The argument format should be <i>{@literal <key>} [values...]</i>
	 * @param channel The channel to send an error message to, if accrues
	 * @param fileLocation The file location of the file to write on
	 * @param defPath The location of a file that will be used if the <code>fileLocation</code> doesn't have the provided key
	 * @param add Whether this method should add or replace a value to the key. <ul>NOTE: adding requires a format type of <i>array<i>.</ul>
	 */
	public static void writeFileCommand(HashMap<String, SSStorage> originalFileValues, String[] args, TextChannel channel,
			String fileLocation, String defPath, boolean add) {
		if (!originalFileValues.containsKey(args[0])) {
			sendErrorEmbed(channel, "לא מצאתי אף התאמה ל- "+args[0]+". האם אתה בטוח שרשמת את שם המפתח נכון?", "$sethelp");
			return;
		}
		String type = originalFileValues.get(args[0]).value;
		if (add && !type.equals("array")) {
			Utils.sendErrorEmbed(channel, "אני יכול לאשר רק מפתחות מסוג `array` בשביל הפקודה הזו.", "$sethelp");
			return;
		}
		
		
		final File guildSaveFile = getGuildSaveFile(channel.getGuild(), fileLocation);
		final String[] values = Arrays.copyOfRange(args, 1, args.length);
		String value = null, output = "Nothing";
		

		if (add) {
			final String tempExistingValue = getValue(fileLocation, channel.getGuild(), defPath, args[0]);
			String existingValue = (tempExistingValue == null) ? "" : tempExistingValue;
			for (int i = 0; i < values.length; i++)
				if (!existingValue.contains(values[i]))
					existingValue += (existingValue.isEmpty() ? "" : ",")+values[i];
			value = existingValue;
			output = Arrays.toString(values);
		} else if (args.length > 1) {
			value = SSStorage.formatValue(values, type);
			if (value == null) {
				//The entered value doesn't seem to be corresponding to the expected one. Make sure your value matches the type.
				sendErrorEmbed(channel, "התוכן שהוכנס לא נראה תואם לסוג המפתח.", "$sethelp");
				return;
			}
			output = value;
		}
		
		try {
			SSStorage.write(args[0], value, guildSaveFile);
		} catch (IOException e) {
			throwFileException(channel, channel.getGuild(), e);
			return;
		}
		
		channel.sendMessageEmbeds(new EmbedBuilder().setTitle("🔧 הצלחה!").addField(output+" שוייך בהצלחה ל- "+args[0], "לעוד מפתחות בקרו בפקודה `$sethelp`", false).setColor(Color.GREEN).build()).queue();
	}
	private static void throwFileException(TextChannel channel, Guild guild, Exception e) {
		System.out.println("NOTICE: Unable to write on file for guild "+guild.getId()+". Exception is being printed.");
		e.printStackTrace();
		channel.sendMessage("התרחשה בעיה תכנית. התראה נשלחה ליוצר שלי, הוא אמור לסדר את זה.").queue();
	}
	
	/**
	 * Gets a {@link HashMap} representing all of the keys in <code>file</code>.
	 * @param path The path to the file, must be an SSS file type.
	 * @return A new HashMap representing all values in <code>file</code>
	 */
	public static HashMap<String, SSStorage> getFileMap(String path) {
		final String settingsDataPath = getDataPath(path);
		final InputStream settingsFileStream = getResourceAsStream(settingsDataPath);
		if (settingsFileStream == null) {
			System.out.println("File "+settingsDataPath+" was not found!");
			return null;
		}
		return SSStorage.getAll(new Scanner(settingsFileStream));
	}
	
}