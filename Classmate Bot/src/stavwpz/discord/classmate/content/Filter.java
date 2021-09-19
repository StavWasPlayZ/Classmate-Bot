package stavwpz.discord.classmate.content;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Used to filter bad words with.
 * 
 * @author Stav c:
 */
public final class Filter extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot() || (Settings.getSetting(event.getGuild(), "filterBypass").equals("true") && event.getMember().hasPermission(Permission.ADMINISTRATOR)))
			return;
		
		String tmpFilters = Settings.getSetting(event.getGuild(), "filters");
		if (tmpFilters == null)
			return;
		String[] filters = tmpFilters.split(","),
			content = event.getMessage().getContentRaw().split("\\s+");
		
		int words = 0;
		StringBuilder censoredSentence = new StringBuilder();
		for (int i = 0; i < content.length; i++) {
			boolean filtered = false;
			for (int j = 0; j < filters.length; j++)
				if (content[i].toLowerCase().contains(filters[j].toLowerCase())) {
					censoredSentence.append("||"+content[i]+"|| ");
					filtered = true;
					words++;
					break;
				}
			if (!filtered)
				censoredSentence.append(content[i]+" ");
		}
		if (words == 0)
			return;
		
		event.getMessage().delete().queue();
		event.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle("⚠ אזהרה")
			.addField("שימוש במילים לא ראויות בשרת", event.getAuthor().getAsMention()+"!\nהמערכת זיהת שימוש ב- **"+words+"** מילים לא רצויות בשרת.\nאבקש ממך להימנע מהם פעם הבאה.", false)
			.setColor(Color.YELLOW).build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
		
		//FIXME "event.getGuild().getTextChannelById(logChannelId)" claims to be null
		String logChannelId = Settings.getSetting(event.getGuild(), "logChannel");
		if (logChannelId != null) {
			TextChannel channel = event.getGuild().getTextChannelById(logChannelId);
			if (channel != null)
				channel.sendMessageEmbeds(new EmbedBuilder().setTitle("⚠ אזהרה")
					.addField("שימוש במילים לא ראויות בשרת", event.getMember().getEffectiveName()+" נתפס ע\"י המערכת עובר את הפילטרים של השרת.\nההודעה:\n"+censoredSentence, false)
					.setColor(Color.YELLOW).build()).queue();
		}
	}
	
}
