package stavwpz.discord.classmate.commandManager;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * A functional interface for command invocation
 * @author Stav c:
 */
@FunctionalInterface
public interface CommandMethod {
	/**
	 * The method for running the provided command
	 * @param event The JDA message event
	 * @param args The arguments provided by the user
	 */
	void run(GuildMessageReceivedEvent event, String[] args);
}
