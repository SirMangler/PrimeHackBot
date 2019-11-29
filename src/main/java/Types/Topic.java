package Types;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class Topic {

	public String topic;
	public List<String> regex;
	public String answer;
	public String wiki_link;
	public String image_url;
	
	public Topic(String topic) {
		this.topic = topic;
	}
	
	public static MessageEmbed displayTopic(Topic t) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(Color.GREEN);
		embed.setTitle("🏷 !"+t.topic);
		String desc = t.answer;
		
		if (t.wiki_link != null)
			embed.setFooter(t.wiki_link, "https://i.imgur.com/TLaIRVU.png");
		
		if (t.image_url != null) 
			embed.setImage(t.image_url);
		
		embed.setDescription(desc);
		
		return embed.build();
	}
}
