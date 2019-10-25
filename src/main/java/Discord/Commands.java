package Discord;

import java.util.ArrayList;

import TopicDetection.Topic;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class Commands {

	static EmbedBuilder template = new EmbedBuilder();
	
	public static Message addTopic(String[] vars) {
		if (vars.length == 1) {
			return new MessageBuilder("Syntax: addtopic [topicname]").build();
		}
		
		boolean added = TopicLoader.addTopic(new Topic(vars[1]));
		MessageBuilder b = new MessageBuilder();
		EmbedBuilder embed = new EmbedBuilder();
		
		if (added) {
			embed.setColor(8913098);
			embed.setTitle("Topic - "+vars[1]+" 🖊");
			embed.setDescription("This topic is empty. See !commands");
			
			b.setEmbed(embed.build());
			b.setContent("Added topic: "+vars[1]);
		} else {
			Message m = getTopic(vars);
			if (m.getEmbeds().isEmpty()) {
				PrimeLogger.severe("Embeds is empty!");
				return new MessageBuilder("Internal error.").build();
			}
			
			b.setEmbed(m.getEmbeds().get(0));
			b.setContent("This topic already exists.");
		}
		
		return b.build();
	}
	
	public static Message removeTopic(String[] vars) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: removeTopic [topicname]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			TopicLoader.removeTopic(t);
			
			return new MessageBuilder("Topic `"+vars[1]+"` was removed!").build();
		}
	}
	
	public static Message getTopic(String[] vars) {
		if (vars.length < 1) {
			return new MessageBuilder("Syntax: getTopic [topicname]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(8913098);
			embed.setTitle("Topic - !"+vars[1]+" 🖊");
			StringBuilder embedcontent = new StringBuilder();
			
			if (t.regex != null) {
				for (int i = 0; i < t.regex.size(); i++) {
					embedcontent.append("Pattern #"+i+" ```"+t.regex.get(i)+"```\n");
				}
			}
			
			embedcontent.append("**answer** = `"+t.answer+"`\n");
			embedcontent.append("**wiki_link** = `"+t.wiki_link+"`\n");
			embedcontent.append("**image_url** = `"+t.image_url+"`\n");
			
			embed.setDescription(embedcontent);
			
			return new MessageBuilder(embed).build();
		}
	}
	
	public static Message setAnswer(String[] vars) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: setAnswer [topicname] [answer]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			String[] answer = new String[vars.length-2];
			for (int i = 2; i < vars.length; i++) {
				answer[i-2] = vars[i];
			}
			
			t.answer = String.join(" ", answer).replaceAll("\\\\n", "\n");
			PrimeLogger.info("Setting topic '%1' answer to '%2'", t.topic, t.answer);
			
			TopicLoader.setTopic(vars[1], t);
			
			Message m = getTopic(vars);
			if (m.getEmbeds().isEmpty()) {
				PrimeLogger.severe("Embeds is empty!");
				return new MessageBuilder("Internal error.").build();
			}
			
			MessageBuilder b = new MessageBuilder();
			
			b.setEmbed(m.getEmbeds().get(0));
			b.setContent("Answer has been set.");
			
			return b.build();
		}
	}
	
	public static Message setImage(String[] vars) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: setImage [topicname] [url]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			t.image_url = vars[2];
			PrimeLogger.info("Setting topic '%1' image to '%2'", t.topic, vars[2]);
			
			TopicLoader.setTopic(vars[1], t);
			
			Message m = getTopic(vars);
			if (m.getEmbeds().isEmpty()) {
				PrimeLogger.severe("Embeds is empty!");
				return new MessageBuilder("Internal error.").build();
			}
			
			MessageBuilder b = new MessageBuilder();
			
			b.setEmbed(m.getEmbeds().get(0));
			b.setContent("Image has been set.");
			
			return b.build();
		}
	}
	
	public static Message setWikiLink(String[] vars) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: setWikiLink [topicname] [wiki_link]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			String[] wiki = new String[vars.length-2];
			for (int i = 2; i < vars.length; i++) {
				wiki[i-2] = vars[i];
			}
			
			t.wiki_link = String.join(" ", wiki);
			PrimeLogger.info("Setting topic '%1' wiki_link to '%2'", t.topic, t.answer);
			
			TopicLoader.setTopic(vars[1], t);
			
			Message m = getTopic(vars);
			if (m.getEmbeds().isEmpty()) {
				PrimeLogger.severe("Embeds is empty!");
				return new MessageBuilder("Internal error.").build();
			}
			
			MessageBuilder b = new MessageBuilder();
			
			b.setEmbed(m.getEmbeds().get(0));
			b.setContent("Added wiki_link.");
			
			return b.build();
		}
	}
	
	public static Message addPattern(String[] vars) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: addPattern [topicname] [pattern]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			String[] pattern = new String[vars.length-2];
			for (int i = 2; i < vars.length; i++) {
				pattern[i-2] = vars[i];
			}
			
			String newpattern = String.join(" ", pattern);
			
			if (t.regex == null)
				t.regex = new ArrayList<String>();
			
			t.regex.add(newpattern);
			PrimeLogger.info("Add pattern '%1' to topic '%2'", newpattern, t.topic);
			
			TopicLoader.setTopic(vars[1], t);
			
			Message m = getTopic(vars);
			if (m.getEmbeds().isEmpty()) {
				PrimeLogger.severe("Embeds is empty!");
				return new MessageBuilder("Internal error.").build();
			}
			
			MessageBuilder b = new MessageBuilder();
			
			b.setEmbed(m.getEmbeds().get(0));
			b.setContent("Added pattern.");
			
			return b.build();
		}
	}
	
	public static Message removePattern(String[] vars) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: removePattern [topicname] [index]").build();
		}
		
		Topic t = TopicLoader.getTopic(vars[1]);
		if (t == null) {
			return new MessageBuilder("Topic `"+vars[1]+"` does not exist!").build();
		} else {
			try {
				int i = Integer.parseInt(vars[2]);
				
				if (t.regex == null)
					return new MessageBuilder("Topic `"+vars[1]+"` does not contain any patterns!").build();
					
				t.regex.remove(i);
				PrimeLogger.info("Removing pattern '%1' from topic '%2'", t.topic, vars[1]);
				
				TopicLoader.setTopic(vars[1], t);
				
				Message m = getTopic(vars);
				if (m.getEmbeds().isEmpty()) {
					PrimeLogger.severe("Embeds is empty!");
					return new MessageBuilder("Internal error.").build();
				}
				
				MessageBuilder b = new MessageBuilder();
				
				b.setEmbed(m.getEmbeds().get(0));
				b.setContent("Removed pattern.");
				
				return b.build();
			} catch (NumberFormatException e) {
				return new MessageBuilder("Given index '"+vars[2]+"' is not a number!").build();
			}
		}
	}
	
	public static Message getListTopics(String[] vars) {
		EmbedBuilder b = new EmbedBuilder();
		b.setTitle("PrimeBot Admin Commands");
		b.setColor(8913098);
		
		StringBuilder strb = new StringBuilder();
		for (Topic t : TopicLoader.getAllTopics())
			strb.append(t.topic+"\n");
		
		if (strb.toString().isEmpty()) strb.append("No topics found. Type `!commands`");
		
		b.setDescription(strb.toString());
		
		return new MessageBuilder(b.build()).build();
	}
	
	public static Message adminCommands(String[] vars) {
		EmbedBuilder b = new EmbedBuilder();
		b.setTitle("PrimeBot Admin Commands");
		b.setDescription(
				"```css\nping /*Pongs!*/```"+
				"```css\naddTopic [topic] /*adds an auto response topic*/```"+
				"```css\nremoveTopic [topic] /*removes an auto response topic*/```"+
				"```css\ngetTopic [topic] /*displays the topic*/```"+
				"```css\nsetAnswer [topic] [answer] /*sets the response*/```"+
				"```css\nsetImage [topic] [image_url] /*Set's the image to embed*/```"+
				"```css\nsetWikiLink [topic] [link] /*sets the wiki_link*/```"+
				"```css\naddPattern [topic] [pattern] /*adds regex pattern*/```"+
				"```css\nremovePattern [topic] [pattern] /*removes regex pattern*/```"+
				"```css\nlistTopics /*Lists all topics.*/```"+
				"```css\ncommands /*Shows this.*/```"
				);
		
		return new MessageBuilder(b).build();
	}
}
