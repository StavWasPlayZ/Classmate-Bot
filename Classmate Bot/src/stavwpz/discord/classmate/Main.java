package stavwpz.discord.classmate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import stavwpz.discord.classmate.commandManager.CommandManager;
import stavwpz.discord.classmate.content.ChatCreator;
import stavwpz.discord.classmate.content.ClassroomCreator;
import stavwpz.discord.classmate.content.Filter;
import stavwpz.discord.classmate.content.RoleAttacher;
import stavwpz.discord.classmate.content.Settings;

/**
 * The main entry to this program.
 * 
 * @author Stav c:
 */
public final class Main {
	
	private static IMainRunner[] runners = {
		new Settings(),
		new ClassroomCreator()
	};
	
	public static void main(String[] args) throws LoginException, FileNotFoundException {
		String token = readToken();
		if (token == null)
			return;
		
		for (int i = 0; i < runners.length; i++)
			runners[i].runOnMain();
		
		JDABuilder.createDefault(token)
			.enableCache(CacheFlag.VOICE_STATE)
			.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
			.setActivity(Activity.listening("your needs"))
			.addEventListeners(new CommandManager(), new Settings(), new RoleAttacher(), new Filter(), new ChatCreator())
			.build();
		
		//Settings.setFileMap();
	}
	
	private static String readToken() {
		final File tokenFile = new File("token.txt");
		if (!tokenFile.exists()) {
			try {
				tokenFile.createNewFile();
				System.out.println("A new \"token.txt\" file has been made in this same directory. Please paste in there your bot token and restart the program to continue.");
			} catch (IOException e) {
				System.out.println("Token was not found in files, and I was not able to create one for you.\nPlease manualy create a \"token.txt\" file in this same directory, paste in there your bot token and restart the program to continue.");
			}
			return null;
		}
		
		Scanner scanner;
		try {
			scanner = new Scanner(tokenFile);
		} catch (FileNotFoundException e) { return null; /* Basically impossible but whatever */ }
		final String token = scanner.nextLine();
		scanner.close();
		return token;
	}

}
