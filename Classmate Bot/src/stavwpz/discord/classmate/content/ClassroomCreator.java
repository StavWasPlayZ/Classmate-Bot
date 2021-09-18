package stavwpz.discord.classmate.content;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import stavwpz.data.sss.SSStorage;
import stavwpz.discord.Utils;
import stavwpz.discord.classmate.IMainRunner;
import stavwpz.discord.classmate.commandManager.CommandManager;

/**
 * A class for the command "addclass" and related who're used to make new subject channels in a server.
 * @author Stav c:
 */
public final class ClassroomCreator implements IMainRunner {

	private static final String STORAGE_PATH = "classroom/classroomTemplate.sss",
		DEF_STORAGE_PATH = "classroom/defaultClassroom.sss";
	private static final HashMap<String, SSStorage> TEMPLATE = Utils.getFileMap(STORAGE_PATH);
	
	@Override
	public void runOnMain() {
		CommandManager.registerCommand("addclass", "<subjectName> [emoji1] [emoji2]", 1, (event, content) -> {
			if (content.length > 3) {
				Utils.sendErrorEmbed(event.getChannel(), "זה נראה כאילו הזנת יותר גורמים משצריך.\nשים לב שאתה מחליף רווחים שלא אמורים להיחשב לגורם אחר ב- U+0020.", null);
				return;
			}
			
			final String prefix = Settings.getSetting(event.getGuild(), "prefix");
			
			String catName = addWhitespace(getValue(event.getGuild(), "categoryName")
					.replace("{subject}", content[0])),
				starterRole = Settings.getSetting(event.getGuild(), "starterRole"),
				emoji2 = (content.length > 2) ? content[2] : null;
			String[] channelNames = getValue(event.getGuild(), "channels").split(","),
				vcs = getValue(event.getGuild(), "vcs").split(",");
			
			if (starterRole == null) {
				Utils.sendErrorEmbed(event.getChannel(), "זה נראה כאילו לא הגדרת את קבוצת התלמידים.\nהשתמש ב- `"+prefix+"set starterRole [קבוצה] על-מנת להגדיר זאת ונסה שנית.", null);
				return;
			}
			if (content.length > 1)
				catName = catName.replace("{emoji1}", content[1]);
			
			
			Role teachRole = event.getGuild().createRole().setName(addWhitespace(getValue(event.getGuild(), "teacherRoleName")).replace("{subject}", content[0]))
				.setColor(0x3498db).setMentionable(true).complete();
			
			Category category = event.getGuild().createCategory(catName)
				.addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, EnumSet.of(Permission.VIEW_CHANNEL))
				.addRolePermissionOverride(event.getGuild().getRoleById(starterRole).getIdLong(), Long.parseLong(getValue(event.getGuild(), "studentPerms")), 0)
				.addRolePermissionOverride(teachRole.getIdLong(), Long.parseLong(getValue(event.getGuild(), "teachPerms")), 0).complete();
			for (int i = 0; i < channelNames.length; i++)
				category.createTextChannel(getNewChannelName(channelNames[i], emoji2, content[0])).queue();
			for (int i = 0; i < vcs.length; i++)
				category.createVoiceChannel(getNewChannelName(vcs[i], emoji2, content[0])).queue();
			
			event.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle("✅ הצלחה!").addField("קטגוריה \""+category.getName()+"\" נוספה בהצלחה ביחד עם דרגת \""+teachRole.getName()+"\".", "אתם יכולים לערוך את שבלונת הערוצים! בקרו בפקודה `"+prefix+"classtemphelp` לעוד מידע", false).setColor(Color.GREEN).build()).queue();
			
		}, "מוסיף מקצוע חדש לשרת", null, EnumSet.of(Permission.MANAGE_CHANNEL));
		
		CommandManager.registerCommand("setclasstemp", "<key> [values...]", 1, (event, content) ->
			Utils.writeFileCommand(TEMPLATE, content, event.getChannel(), STORAGE_PATH, DEF_STORAGE_PATH, false)
		, null, "classtemphelp", EnumSet.of(Permission.MANAGE_CHANNEL), false);
		
		CommandManager.registerCommand("addclasstemp", "<key> [values...]", 1, (event, content) ->
			Utils.writeFileCommand(TEMPLATE, content, event.getChannel(), STORAGE_PATH, DEF_STORAGE_PATH, true)
		, null, "classtemphelp", EnumSet.of(Permission.MANAGE_CHANNEL), false);
		
		CommandManager.registerCommand("classtemphelp", null, 0, (event, content) ->
			event.getChannel().sendMessageEmbeds(Utils.helpEmbedGenerator("🏫 עוזר השבלונות", "הנה רשימת המפתחות שניתן לשנות:", TEMPLATE.keySet(), (key) ->
				Utils.getFormattedEntry(key, TEMPLATE.get(key), "🗝", true)
			, Utils.getFooter("setclasstemp", "addclasstemp", event.getGuild()),
				"**רשימת משתנים**\nמשתנים הם טקסט שניתן להוסיפו לכותרות, והוא מתחלף בצורה אוטומטית עם התוכן המצורף לו. מביניהם\n"+getVariableList(new Variable("{subject}", "שם השיעור הרצוי"), new Variable("{emoji1}", "אמוג'י מס' 1, בדרך כלל האחד שמשומש לשם הקטגוריה"), new Variable("{emoji2}", "אמוג'י מס' 2, בדרך כלל האחד שמשומש לשם ערוץ")))
				.build()).queue()
		, null, "classtemphelp", EnumSet.of(Permission.MANAGE_CHANNEL), false);
	}
	private static String getVariableList(Variable... variables) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < variables.length; i++)
			builder.append(String.format("✖ `%s`: %s\n", variables[i].varName, variables[i].description));
		return builder.toString();
	}
	private static String getNewChannelName(String name, String emoji, String subject) {
		return addWhitespace(name).replace("{emoji2}", (emoji == null) ? "" : emoji).replace("{subject}", subject);
	}
	private static String getValue(Guild guild, String key) {
		return Utils.getValue(STORAGE_PATH, guild, DEF_STORAGE_PATH, key);
	}
	private static String addWhitespace(String str) {
		return str.replace("U+0020", " ");
	}
	
}
final class Variable {
	public String varName, description;
	public Variable(String varName, String description) {
		this.varName = varName;
		this.description = description;
	}
}