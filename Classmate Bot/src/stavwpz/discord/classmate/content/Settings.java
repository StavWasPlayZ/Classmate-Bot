package stavwpz.discord.classmate.content;

import java.util.EnumSet;
import java.util.HashMap;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import stavwpz.data.sss.SSStorage;
import stavwpz.discord.Utils;
import stavwpz.discord.classmate.IMainRunner;
import stavwpz.discord.classmate.commandManager.CommandManager;

/**
 * A class for saving preferences for each guild using the bot.<br>
 * It saves preferences to the system in the {@link SSStorage SSS} file format in case of my trash host PC crashing or running
 * out of battery.
 * 
 * @author Stav c:
 */
public final class Settings implements IMainRunner {
	
	private static final String SETTINGS_DATA_LOCATION = "settings/settings.sss",
		DEF_DATA_LOCATION = "settings/defaultSettings.sss";
	private static final HashMap<String, SSStorage> SETTINGS = Utils.getFileMap(SETTINGS_DATA_LOCATION);
	
	/**
	 * Gets a setting by its key name.
	 * @param guild The guild to get the setting from
	 * @param settingName The key to the setting
	 * @return The setting's value
	 */
	public static String getSetting(Guild guild, String settingName) {
		return Utils.getValue(SETTINGS_DATA_LOCATION, guild, DEF_DATA_LOCATION, settingName);
	}
	
	
	@Override
	public void runOnMain() {
		CommandManager.registerCommand("set", "<key> [value...]", 1, (event, content) ->
			Utils.writeFileCommand(SETTINGS, content, event.getChannel(), SETTINGS_DATA_LOCATION, DEF_DATA_LOCATION, false)
		, "מגדיר את הבוט לרצונך", "sethelp", EnumSet.of(Permission.ADMINISTRATOR));
		
		CommandManager.registerCommand("add", "<key> <values...>", 2, (event, content) ->
			Utils.writeFileCommand(SETTINGS, content, event.getChannel(), SETTINGS_DATA_LOCATION, DEF_DATA_LOCATION, true)
		, null, "sethelp", EnumSet.of(Permission.ADMINISTRATOR), false);
		
		CommandManager.registerCommand("sethelp", null, 0, (event, content) -> 
			event.getChannel().sendMessageEmbeds(Utils.helpEmbedGenerator("🔑 עוזר המפתחות", "רשימת המפתחות הינה:", SETTINGS.keySet(), (key) ->
				Utils.getFormattedEntry(key, SETTINGS.get(key), "🗝", true)
			, Utils.getFooter("set", "add", event.getGuild()), null).build()).queue()
		, null, null, EnumSet.of(Permission.ADMINISTRATOR), false);
	}
}