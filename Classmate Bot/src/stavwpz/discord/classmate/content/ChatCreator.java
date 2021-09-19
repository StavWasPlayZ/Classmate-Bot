package stavwpz.discord.classmate.content;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Used for creating a new "chat" {@link TextChannel} to when a user joins a {@link VoiceChannel}. 
 * @author Stav c:
 */
public final class ChatCreator extends ListenerAdapter {
	
	private static final HashMap<VoiceChannel, TextChannel> TEXT_CHANNELS = new HashMap<VoiceChannel, TextChannel>(); 
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if (Settings.getSetting(event.getGuild(), "createChatChannel").equals("false"))
			return;
		
		final VoiceChannel channel = event.getChannelJoined();
		if (channel.getMembers().size() != 1)
			return;
		
		TEXT_CHANNELS.put(event.getChannelJoined(), event.getGuild().createTextChannel("צאט", channel.getParent()).complete());
	}
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		VoiceChannel channel = event.getChannelLeft();
		if (channel.getMembers().size() != 0)
			return;
	
		TextChannel channel2Remove = TEXT_CHANNELS.get(channel);
		try {
			channel2Remove.delete().queue();
		} catch (NullPointerException e) {} // No idea why but sometimes the value is null and the action was successful.
		TEXT_CHANNELS.remove(channel);
		return;
	}
	
}