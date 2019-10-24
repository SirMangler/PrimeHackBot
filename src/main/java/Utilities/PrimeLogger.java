package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class PrimeLogger {
	
	static Queue<String> queue = new LinkedList<String>();
	static Thread filelogger_thr;
	
	public static void info(String message, String... variables) {
		log("[INFO] "+message, variables);
	}
	
	public static void severe(String message, String... variables) {
		log("[SEVERE] "+message, variables);
	}
	
	public static void debug(String message, String... variables) {
		if (Configuration.debug) {
			log("[DEBUG] "+message, variables);
		}
	}
	
	public static void log(String message, String... variables) {
		for (int i = 0; i < variables.length; i++) {
			message = message.replace("%"+(i+1), variables[i]);
		}
		
		System.out.println("["+getShortTimeStamp()+"]"+message);
		queue.add("["+getFullTimeStamp()+"]"+message);
		
		if (filelogger_thr == null || !filelogger_thr.isAlive()) {
			filelogger_thr = new Thread(new FileLogger());
			filelogger_thr.start();
		}
	}
	
	public static String getShortTimeStamp() {
	    return LocalDateTime.now()
	       .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	
	public static String getFullTimeStamp() {
	    return LocalDateTime.now()
	       .format(DateTimeFormatter.ofPattern("E/MMM HH:mm:ss"));
	}
}

class FileLogger implements Runnable {

	static Path cfgpath = Paths.get(System.getProperty("user.dir"), "primebot.log");
	
	@Override
	public void run() {
		try (FileWriter fw = new FileWriter(cfgpath.toString(), true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
		{
			while (true) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (!PrimeLogger.queue.isEmpty()) {
					String log;
					while ((log = PrimeLogger.queue.poll()) != null) {
						out.println(log);
						out.flush();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
