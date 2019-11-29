package Discord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import Utilities.Configuration;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import Utilities.WarnsLoader;
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

	public static List<Role> botroles = new ArrayList<Role>();

	public static JDA jda;
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
			
			WarnsLoader.loadWarns();
			
			if (Configuration.token == null || Configuration.token.isEmpty()) {
				PrimeLogger.severe("No token found! Please add one to the primebot.cfg file!");
				System.exit(0);
			}
			
			PrimeLogger.info("Connecting to Discord");
			jda = new JDABuilder(AccountType.BOT).setToken(Configuration.token).buildBlocking();
			
			PrimeLogger.info("Adding EventHandler");
			jda.addEventListener(new EventHandler());

			Configuration.bot_controllers.forEach(controller -> {
				Role r = jda.getRoleById(controller);
				if (r == null) {
					PrimeLogger.severe("Cannot retrieve bot controller role: %1", controller);
				} else botroles.add(r);			
			});
			
			jda.getPresence().setGame(Game.of(GameType.LISTENING, "the depths of space"));	
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	

}