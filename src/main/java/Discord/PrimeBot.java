package Discord;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import Utilities.Configuration;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import Utilities.WarnsLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * @author SirMangler
 *
 * @date 22 Oct 2019
 */
public class PrimeBot {

	public static List<Role> botroles = new ArrayList<Role>();

	public static JDA jda;
	public static void main(String[] args) {	
		if (args.length == 1 && args[0].equalsIgnoreCase("debug"))
			Configuration.debug = true;
		
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
			jda = JDABuilder.createDefault(Configuration.token).build();
			jda.awaitReady();
			
			PrimeLogger.info("Adding EventHandler");
			jda.addEventListener(new EventHandler());

			Configuration.bot_controllers.forEach(controller -> {
				Role r = jda.getRoleById(controller);
				if (r == null) {
					PrimeLogger.severe("Cannot retrieve bot controller role: %1", controller);
				} else botroles.add(r);			
			});
			
			jda.getPresence().setActivity(Activity.listening("the depths of space"));

			try {
				System.setProperty("java.awt.headless", "true");
				
				loadFonts();
			} catch (FontFormatException | IOException e) {
				PrimeLogger.severe("Failed to load fonts.");
				e.printStackTrace();
			}
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader =  
	                new BufferedReader(new InputStreamReader(System.in)); 
			
			String line;
				while ((line = reader.readLine()) != null) {
					if (line.equalsIgnoreCase("shutdown")) {
						jda.shutdown();
						System.exit(0);
					}
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void loadFonts() throws FontFormatException, IOException {
		GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		File pig = new File("pig.ttf");
		File sub = new File("sub.ttf");
		
		if (pig.exists())
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("pig.ttf")));
		     
		if (sub.exists())
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("sub.ttf")));
	}

}