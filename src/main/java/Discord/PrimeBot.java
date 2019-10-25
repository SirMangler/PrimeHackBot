package Discord;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import Utilities.Configuration;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Role;

/**
 * @author SirMangler
 *
 * @date 22 Oct 2019
 */
public class PrimeBot {

	public static Role mod;
	public static Role admin;
	private static JDA jda;
	public static void main(String[] args) {
		try {
			try {
				Configuration.loadConfiguration();
			} catch (IOException e) {
				PrimeLogger.severe("Failed to load configuration");
				e.printStackTrace();
				System.exit(0);
			}
			
			try {
				TopicLoader.loadTopics();
			} catch (IOException e) {
				PrimeLogger.severe("Error when initially loading topics");
				e.printStackTrace();
			}
			
			if (Configuration.token == null || Configuration.token.isEmpty()) {
				PrimeLogger.severe("No token found! Please add one to the primebot.cfg file!");
				System.exit(0);
			}
			
			PrimeLogger.info("Connecting to Discord");
			jda = new JDABuilder(AccountType.BOT).setToken(Configuration.token).buildBlocking();
			
			PrimeLogger.info("Adding EventHandler");
			jda.addEventListener(new EventHandler());
			
			mod = jda.getRoleById("385208116221968385"); //jda.getRoleById("290916002231746571");// // // primehack mod
			admin = jda.getRoleById("385203760886054912");		
			
			if (mod == null) 
				PrimeLogger.severe("The moderator role cannot be found!");
			
			if (admin == null)
				PrimeLogger.severe("The admin role cannot be found!");
			
			jda.getPresence().setGame(Game.of(GameType.LISTENING, "the depths of space"));
			
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	

}