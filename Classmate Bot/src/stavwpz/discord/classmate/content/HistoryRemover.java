package stavwpz.discord.classmate.content;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;
import stavwpz.discord.classmate.IMainRunner;
import stavwpz.discord.classmate.commandManager.CommandManager;

public class HistoryRemover implements IMainRunner {

	@Override
	public void runOnMain() {
		CommandManager.registerCommand("clean", null, 0, (event, content) -> {
			event.getChannel().deleteMessages(event.getChannel().getHistory().retrievePast(100).complete()).queue();
		}, "מנקה את אותו הצ'אט שהפקודה נכתבה בה", null, EnumSet.of(Permission.MANAGE_CHANNEL));
	}

}
