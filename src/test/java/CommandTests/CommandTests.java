package CommandTests;

import Discord.EventHandler;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author SirMangler
 *
 * @date 24 Oct 2019
 */
public class CommandTests {

	public static void main(String[] args) {
		EventHandler e = new EventHandler();
		
		System.out.println(1 > 2);
		System.out.println(1 < 2);
		
		MessageBuilder b = new MessageBuilder();
		Message[] messages = new Message[] 
				{ 
						b.setContent("ping").build(),
						b.setContent("addtopic carrot").build(),
						b.setContent("setanswer carrot lol hi how are you").build(),
						b.setContent("addpattern carrot (some regex pattern)").build(),
						b.setContent("removepattern carrot 0").build(),
						b.setContent("removetopic carrot").build() 
				};
		
		for (Message msg : messages) {
			String input = msg.getContentRaw();
			String output = e.parseCommand(msg.getContentRaw()).getContentRaw();
			System.out.println(">> "+input+"\n<< "+output);
		}
	}

}
