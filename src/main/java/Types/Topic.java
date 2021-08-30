package Types;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class Topic {

	public String topic;
	public List<String> regex;
	public String answer;
	public String image_url;
	public String[] aliases;
	public List<String> channels;
	
	public Topic(String topic) {
		this.topic = topic;
	}
	
	public static MessageEmbed displayTopic(Topic t, String invoker) {
		String aliases = "";
		
		if (t.aliases != null) {
			if (t.aliases.length != 0)
				aliases = ", !"+String.join(", !", t.aliases)+"";
		}

		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(Color.GREEN);
		embed.setTitle("🏷 !"+t.topic+aliases);

		if (invoker != null)
			embed.setFooter(invoker, "https://i.imgur.com/TLaIRVU.png");
		
		if (t.image_url != null) 
			embed.setImage(t.image_url);
		
		embed.setDescription(t.answer);
		
		return embed.build();
	}
}
