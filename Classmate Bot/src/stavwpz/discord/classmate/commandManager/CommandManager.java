package stavwpz.discord.classmate.commandManager;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import stavwpz.discord.Utils;

public class CommandManager extends ListenerAdapter {
	
	private static final HashMap<String, Command> COMMANDS = mapMake();
	private static HashMap<String, Command> mapMake() {
		final HashMap<String, Command> map = new HashMap<String, Command>();
		map.put("help", new Command(null, 0, (event, content) -> 
			event.getChannel().sendMessageEmbeds(Utils.helpEmbedGenerator("💁‍♂️ עוזר הפקודות", "הפקודות הרשומות אצלי הינן:", map.keySet(), (key) -> {
				Command command = map.get(key);
				if (!command.showOnList)
					return "";
				//TODO replace '$' with guild prefix
				return String.format("• `%s%s`%s%s\n",
					"$", key, (command.arguments == null) ? "" : " `"+command.arguments+"`" ,(command.description == null) ? "" : " "+command.description);
			}, null, null).build()).queue()
		, "מספק את רשימת הפקודות בשרת", null, null, true));
		return map;
	}
	/**
	 * Register a command to this project
	 * @param name The name of this command to be called, for instance if a guild's prefix is '$' then
	 * 	$<code>name</code> will trigger it in chat.
	 * @param arguments The "description" arguments to be passed in this command,
	 * 	such as "<i>{@literal <subject>} [categoryEmoji] [channelEmoji] </i>" 
	 * @param minLength The minimum amount of your arguments, usually if you wrote your first section of <code>arguments</code>
	 * 	with triangular brackets, this will be the amount.
	 * @param onInvoke The method to be executed on command being called
	 * @param description The descriptive information about this command, will appear on command "help"
	 */
	public static void registerCommand(String name, String arguments, int minLength, CommandMethod onInvoke, String description) {
		COMMANDS.put(name, new Command(arguments, minLength, onInvoke, description, null, null, true));
	}
	public static void registerCommand(String name, String arguments, int minLength, CommandMethod onInvoke, String description,
			String helpCommand) {
		COMMANDS.put(name, new Command(arguments, minLength, onInvoke, description, helpCommand, null, true));
	}
	public static void registerCommand(String name, String arguments, int minLength, CommandMethod onInvoke, String description,
			String helpCommand, EnumSet<Permission> requiredPermissions) {
		COMMANDS.put(name, new Command(arguments, minLength, onInvoke, description, helpCommand, requiredPermissions, true));
	}
	public static void registerCommand(String name, String arguments, int minLength, CommandMethod onInvoke, String description,
			String helpCommand, EnumSet<Permission> requiredPermissions, boolean isVisible) {
		COMMANDS.put(name, new Command(arguments, minLength, onInvoke, description, helpCommand, requiredPermissions, isVisible));
	}
	
	/**
	 * Gets a command from the {@link COMMANDS commands} map by <code>key</code>. 
	 * @param key The key attached to the value
	 * @return The value of <code>key</code>
	 */
	public static Command getCommand(String key) {
		return COMMANDS.get(key);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		final String[] content = event.getMessage().getContentRaw().split("\\s+");
		if (content.length == 0 || event.getAuthor().isBot())
			return;
		//TODO replace '$' with guild prefix
		final String calledCommand = content[0].substring("$".length());		

		if (COMMANDS.containsKey(calledCommand)) {
			Command command = COMMANDS.get(calledCommand);
			String[] args = Arrays.copyOfRange(content, 1, content.length);
			if (validateResponse(command, event.getMember(), event.getChannel(), args))
				command.onInvoke.run(event, args);
		}
	}
	private static boolean validateResponse(Command command, Member member, TextChannel channel, String[] args) {
		if (member.getUser().isBot())
			return false;
		if (command.permissions != null && !member.hasPermission(command.permissions)) {
			Utils.sendErrorEmbed(channel, "נראה שאינך רשאי לפעולה זו. תוכל לקרוא למנהל השרת לעשות זאת במקומך.", command.helpCommand);
			return false;
		}
		if (args.length < command.minLength) {
			Utils.sendErrorEmbed(channel, "לא יכולתי להבין אותך.", command.helpCommand);
			return false;
		}
		return true;
	}
	
}
