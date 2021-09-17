package stavwpz.discord.classmate.content;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Used to give new members a role attached with the <i>starterRole</i> {@link Settings setting}.
 * 
 * @author Stav c:
 */
public final class RoleAttacher extends ListenerAdapter {
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Role role = event.getGuild().getRoleById(Settings.getSetting(event.getGuild(), "starterRole"));
		if (role != null)
			event.getGuild().addRoleToMember(event.getUser().getIdLong(), role).queue();
	}
	
}
