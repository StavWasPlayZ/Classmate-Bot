package stavwpz.discord.classmate.commandManager;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;

/**
 * An object for storing Discord commands
 * @author Stav c:
 */
//TODO add description
public final class Command {
	public int minLength;
	public boolean showOnList;
	public CommandMethod onInvoke;
	public String arguments, helpCommand, description;
	public EnumSet<Permission> permissions;
	
	public Command(String arguments, int minLength, CommandMethod onInvoke, String description, String helpCommand, EnumSet<Permission> requiredPermissions, boolean isVisible) {
		this.arguments = arguments;
		this.minLength = minLength;
		this.onInvoke = onInvoke;
		this.description = description;
		this.helpCommand = helpCommand;
		permissions = requiredPermissions;
		showOnList = isVisible;
	}
}
