package Discord.Commands;

import java.io.File;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.TextChannel;

/**
 * @author SirMangler
 *
 * @date 2 Jan 2021
 */
public class ReadTheWiki {

	public static void sendRant(TextChannel c) {
		File read = new File("read.png");
		File the = new File("the.png");
		File wiki = new File("wiki.png");
		File arrows = new File("arrows.png");
		
		if (!read.exists() || !the.exists() || !wiki.exists() || !arrows.exists())
			return;
		
		c.sendFile(read).queue(s1 -> {
			c.sendFile(the).queueAfter(1000, TimeUnit.MILLISECONDS, s2 -> {
				c.sendMessage("<https://github.com/shiiion/dolphin/wiki>").addFile(wiki).queueAfter(1500, TimeUnit.MILLISECONDS, s3 -> {
					c.sendMessage("<https://github.com/shiiion/dolphin/wiki>").addFile(arrows).queue();
					c.sendMessage("<https://github.com/shiiion/dolphin/wiki>").queue();
					c.sendMessage("<https://github.com/shiiion/dolphin/wiki>").queue();
				});
			});
		});
	}
}
