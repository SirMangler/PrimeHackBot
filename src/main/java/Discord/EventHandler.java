package Discord;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import TopicDetection.Topic;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author SirMangler
 *
 * @date 23 Oct 2019
 */
public class EventHandler extends ListenerAdapter {

	Consumer<Message> queueSuccess = (response) -> PrimeLogger.info("<%1> %2", response.getTextChannel().getName(), response.getContentDisplay());
	Consumer<Throwable> queueError = (error) -> PrimeLogger.severe("Could not send message: %1", error.getMessage());

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getAuthor() == e.getJDA().getSelfUser()) return;

		if (e.getTextChannel().canTalk()) {
			if (e.getMessage().getContentRaw().startsWith("!")) {
				TopicLoader.getAllTopics().forEach(topic -> {
					if (e.getMessage().getContentDisplay().startsWith("!"+topic.topic)) {
						e.getTextChannel().sendMessage(Topic.displayTopic(topic)).queue(queueSuccess, queueError);
						return;
					}
				});

				if (e.getMember().getRoles().contains(PrimeBot.admin)
						|| e.getMember().getRoles().contains(PrimeBot.mod)) {
					Message message = parseCommand(e.getMessage().getContentRaw().substring(1));

					if (message != null)
						e.getTextChannel().sendMessage(message).queue(queueSuccess, queueError);
					return;
				}
			}

			TopicLoader.getAllTopics().forEach(topic -> {
				if (topic.regex != null)
					topic.regex.forEach(pattern -> {
						if (Pattern.matches(pattern, e.getMessage().getContentRaw())) {
							e.getTextChannel().sendMessage(Topic.displayTopic(topic)).queue(queueSuccess, queueError);
							return;
						}
					});		
			});

		}
	}

	public Message parseCommand(String line) {
		String[] vars = null;
		if (line.contains(" ")) {
			vars = line.split(" ");
		} else {
			vars = new String[] { line };
		}

		switch (vars[0].toLowerCase()) {
		case "ping":
			return new MessageBuilder("Pong").build();
		case "addtopic":
			return Commands.addTopic(vars);
		case "removetopic":
			return Commands.removeTopic(vars);
		case "gettopic":
			return Commands.getTopic(vars);
		case "setimage":
			return Commands.setImage(vars);
		case "setanswer":
			return Commands.setAnswer(vars);
		case "setwikilink":
			return Commands.setWikiLink(vars);
		case "addpattern":
			return Commands.addPattern(vars);
		case "removepattern":
			return Commands.removePattern(vars);
		case "commands":
			return Commands.adminCommands(vars);
		}

		return null;
	}
}