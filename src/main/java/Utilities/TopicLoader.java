package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import Types.Topic;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class TopicLoader {

	static Path topicspath = Paths.get(System.getProperty("user.dir"), "topics.cfg");
	
	static List<Topic> topics = new ArrayList<Topic>();

	public static void loadTopics() throws IOException {
		PrimeLogger.info("Loading Topics");
		
		if (!Files.exists(topicspath)) {
			PrimeLogger.info("No configuration found. Making new file.");
			Files.createFile(topicspath);
			
			return;
		}
		
		List<String> lines = Files.readAllLines(topicspath);
		
		Topic t = null;
		StringBuilder answer = null;
		for (String line : lines) {
			if (line.isEmpty() || line.equalsIgnoreCase("")) {
				if (answer == null) {
					if (t != null)
						topics.add(t);
					
					t = null;
					continue;
				}				
			}
			
			if (line.startsWith("[")) {
				t = new Topic(line.substring(1, line.indexOf("]")));
				continue;
			}
			
			if (line.startsWith("regex = ")) {
				if (t.regex == null) t.regex = new ArrayList<String>();
				t.regex.add(line.substring(8));
				continue;
			}
				
			if (line.startsWith("aliases = ")) {
				String alias = line.substring(10);
				
				if (!alias.isEmpty()) {
					if (alias.contains(";"))
						t.aliases = alias.split(";");
					else t.aliases = new String[] { alias };
				}

				continue;
			}
			
			if (line.startsWith("answer = ")) {
				if (line.endsWith("\""))
					t.answer = line.substring(9);
				else {
					answer = new StringBuilder();
					answer.append(line.substring(10));
				}
				continue;
			}
			
			if (line.startsWith("image_url = \"")) {
				t.image_url = line.substring(13);
				continue;
			}
			
			if (answer != null) {
				if (line.endsWith("\"")) {
					answer.append("\n"+line.substring(0, line.length()-1));
					
					t.answer = answer.toString();
					answer = null;
				} else {
					answer.append("\n"+line);
				}
			}
		}
		
		if (t != null) {
			topics.add(t);
		}
	}

	public static void saveTopics() {
		PrimeLogger.info("Saving Topics");
		
		if (!Files.exists(topicspath)) {
			try {
				Files.createFile(topicspath);
			} catch (IOException e) {
				PrimeLogger.severe("Could not create '%1'. Insufficient permissions?", topicspath.toAbsolutePath().toString());
				e.printStackTrace();
			}
			
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		for (Topic t : topics) {
			builder.append("["+t.topic+"]\r\n");
			
			if (t.regex != null)
				t.regex.forEach(regex -> {
					builder.append("regex = "+regex+"\r\n");
				});
			
			if (t.answer != null)
				builder.append("answer = \""+t.answer+"\"\r\n");
			
			if (t.aliases != null)
				builder.append("aliases = "+String.join(";", t.aliases)+"\r\n");
			
			if (t.image_url != null)
				builder.append("image_url = "+t.image_url+"\r\n");
			
			builder.append("\r\n");
		}
		
		try {
			Files.write(topicspath, builder.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			PrimeLogger.severe("Could not write to topics.cfg");
			e.printStackTrace();
		}
	}
	
	public static boolean addTopic(Topic t) {
		PrimeLogger.info("Adding topic: %1", t.topic);
		
		if (topics.contains(t))
			return false;
		
		topics.add(t);		
		
		saveTopics();
		return true;
	}
	
	public static void removeTopic(Topic t) {
		PrimeLogger.info("Removing topic: %1", t.topic);
		topics.remove(t);

		saveTopics();
	}
	
	public static List<Topic> getAllTopics() {
		if (topics == null) {
			try {
				loadTopics();
			} catch (IOException e) {
				PrimeLogger.severe("Failed to load Topics!");
				e.printStackTrace();
			}
		}
		
		return topics;
	}

	public static Topic getTopic(String name) {
		for (Topic topic : getAllTopics()) {
			if (topic.topic.equalsIgnoreCase(name)) return topic;
		}
		
		return null;
	}
	
	public static Topic setTopic(String name, Topic newtopic) {
		if (topics == null) {
			try {
				loadTopics();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Topic t = getTopic(name);
		if (t == null) {
			return null;
		}
		
		topics.remove(t);
		topics.add(newtopic);
		
		saveTopics();
		
		return null;
	}
}
