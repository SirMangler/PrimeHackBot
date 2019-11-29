package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import Types.WarnMember;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class WarnsLoader {

	static Path warnspath = Paths.get(System.getProperty("user.dir"), "warns.cfg");
	
	static List<WarnMember> members = null;
	
	public static void loadWarns() {
		if (!Files.exists(warnspath)) {
			try {
				Files.createFile(warnspath);
			} catch (IOException e) {
				PrimeLogger.severe("Could not create '%1'. Insufficient permissions?", warnspath.toAbsolutePath().toString());
				e.printStackTrace();
			}
			
			return;
		}
		
		members = new ArrayList<WarnMember>();
		
		List<String> lines;
		try {
			lines = Files.readAllLines(warnspath);
		
			WarnMember warnmember = null;
			for (String line : lines) {
				if (line.startsWith("[")) {
					warnmember = new WarnMember();
					warnmember.user_id = line.substring(1, line.indexOf("]"));
	
					continue;
				}
				
				if (line.isEmpty() || line.equalsIgnoreCase("\n")) {
					members.add(warnmember);
					warnmember = null;
					
					continue;
				}
				
				
				if (line.startsWith("warn = ")) {
					if (warnmember == null) {
						PrimeLogger.severe("WarnMember is null when loading warns!");
						continue;
					} 
					
					warnmember.warnings.add(line.substring(7));
				}
			}
			
			if (warnmember != null)
				members.add(warnmember);		
		} catch (IOException e) {
			PrimeLogger.severe("Failed to load Warns!");
			e.printStackTrace();
		}
	}
	
	public static void saveWarns() {
		PrimeLogger.info("Saving Warnings");
		
		if (!Files.exists(warnspath)) {
			try {
				Files.createFile(warnspath);
			} catch (IOException e) {
				PrimeLogger.severe("Could not create '%1'. Insufficient permissions?", warnspath.toAbsolutePath().toString());
				e.printStackTrace();
			}
			
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		for (WarnMember m : members) {
			builder.append("["+m.user_id+"]\r\n");
			
			if (m.warnings != null)
				m.warnings.forEach(warn -> {
					builder.append("warn = "+warn+"\r\n");
				});
			
			builder.append("\r\n");
		}
		
		try {
			Files.write(warnspath, builder.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			PrimeLogger.severe("Could not write to topics.cfg");
			e.printStackTrace();
		}
	}

	public static WarnMember getWarnMember(String user_id) {
		if (members == null)
			loadWarns();
		
		for (WarnMember member : members) {
			if (member.user_id.equals(user_id)) return member;
		}
		
		return null;
	}
	
	public static int addWarning(String id, String warning) {
		PrimeLogger.info("Writing warning - id '%1': '%2'", id, warning);
		
		if (members == null)
			loadWarns();
			
		WarnMember member = getWarnMember(id);
		
		if (member != null) {
			members.remove(member);
		} else {
			member = new WarnMember();
			member.user_id = id;
		}
		
		member.warnings.add(warning);
		members.add(member);	
		
		saveWarns();
		
		return member.warnings.size();
	}
	
	public static int removeWarning(String id, int index) {
		PrimeLogger.info("Trying to remove warning '%2' from user '%1'", id, index+"");
		
		if (members == null)
			loadWarns();
			
		WarnMember member = getWarnMember(id);
		
		if (member != null) {
			members.remove(member);
		} else {
			member = new WarnMember();
			member.user_id = id;
		}
		
		member.warnings.remove(index);
		members.add(member);	
		
		saveWarns();
		
		return member.warnings.size();
	}
	
	public static void removeWarning(WarnMember member) {
		if (members == null) loadWarns();
		
		PrimeLogger.info("Removing warnings from: %1", member.user_id);
		members.remove(member);

		saveWarns();
	}
	
	public static List<WarnMember> getAllWarns() {
		if (members == null)
			loadWarns();

		return members;
	}
}
