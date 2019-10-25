package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SirMangler
 *
 * @date 3 Apr 2019
 */
public class Configuration {

	public static String token;
	public static boolean debug;
	public static List<String> bot_controllers = new ArrayList<String>();
	
	static Path cfgpath = Paths.get(System.getProperty("user.dir"), "primebot.cfg");
	
	public static void loadConfiguration() throws IOException {
		PrimeLogger.info("Loading Configuration");
		
		if (!Files.exists(cfgpath)) {
			PrimeLogger.info("No configuration found. Making new file.");
			createDefaultConfiguration();
			return;
		}
		
		List<String> lines = Files.readAllLines(cfgpath);
		
		for (String line : lines) {
			if (line.isEmpty() || line.equalsIgnoreCase("") || !line.contains("="))
				continue;
			
			String[] keyval = line.split("=");
			
			String val;
			if (keyval.length == 1) val = "";
			else val = keyval[1];
			
			switch (keyval[0].toLowerCase()) {
			case "token":				
				token = val;
				break;
			case "bot-controller":
				bot_controllers.add(val);
				break;
			}
		}
	}
	
	public static void createDefaultConfiguration() throws IOException {
		Files.createFile(cfgpath);
		
		token = "";
		
		saveConfiguration();
	}
	
	public static void saveConfiguration() throws IOException {
		PrimeLogger.info("Saving configuration.");
		
		StringBuilder b = new StringBuilder();
		b.append("token="+token+"\n\r");
		for (String controller : bot_controllers) {
			b.append("bot-controller="+controller+"\n\r");
		}
		
		Files.write(cfgpath, b.toString().getBytes());
	}
	
}
