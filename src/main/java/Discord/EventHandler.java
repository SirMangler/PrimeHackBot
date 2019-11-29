package Discord;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import Discord.Commands.ModeratorCommands;
import Discord.Commands.TopicCommands;
import Types.Topic;
import Utilities.Configuration;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
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
				for (Role role : PrimeBot.botroles) {			
					if (e.getMember().getRoles().contains(role)) {
						Message message = parseCommand(e.getMessage().getContentRaw().substring(1));

						if (message == null) {
							Member m = null;
							if (e.getMessage().getMentionedMembers().size() != 0)
								m = e.getMessage().getMentionedMembers().get(0);
								
							message = parseModerator(
										e.getMessage().getContentRaw().substring(1), m, e.getMessage());
						}
						
						if (message != null) {
							e.getTextChannel().sendMessage(message).queue(queueSuccess, queueError);
							return;
						}
					}
				}
			}
			
			TopicLoader.getAllTopics().forEach(topic -> {
				if (topic.regex != null)
					for (String pattern : topic.regex) {
						if (Pattern.matches(pattern, e.getMessage().getContentRaw().toLowerCase())) {
							e.getTextChannel().sendMessage(Topic.displayTopic(topic)).queue(queueSuccess, queueError);
							return;
						}
					}	
				
				if (e.getMessage().getContentDisplay().toLowerCase().startsWith("!"+topic.topic)) {
					e.getTextChannel().sendMessage(Topic.displayTopic(topic)).queue(success -> {
						PrimeLogger.info("<%1> %2", success.getTextChannel().getName(), success.getContentDisplay());
						
						if (e.getGuild().getSelfMember().hasPermission(new Permission[] { Permission.MESSAGE_MANAGE }))
							e.getMessage().delete().complete();
					}, queueError);
					return;
				}
			});
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getMessageId().equals(Configuration.gate_message_id)) {
			if (e.getReactionEmote().getEmote().getId().equals(Configuration.gate_emote_id)) {
				Role verified = e.getGuild().getRoleById(Configuration.gate_role_id);
				if (verified == null) {
					PrimeLogger.severe("Couldn't retrieve verified role.");
				}
				
				e.getGuild().getController().addSingleRoleToMember(e.getMember(), verified).queue(success -> {}, failure -> {
					failure.printStackTrace();
					e.getGuild().getTextChannelById("562547073459945489")
						.sendMessage("Couldn't give verified role to: `"+e.getMember().getEffectiveName()+"`. Error: "+failure.getMessage()).complete();
				});;
			}
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
			return TopicCommands.addTopic(vars);
		case "removetopic":
			return TopicCommands.removeTopic(vars);
		case "gettopic":
			return TopicCommands.getTopic(vars);
		case "listtopics":
			return TopicCommands.getListTopics(vars);
		case "setimage":
			return TopicCommands.setImage(vars);
		case "setanswer":
			return TopicCommands.setAnswer(vars);
		case "setwikilink":
			return TopicCommands.setWikiLink(vars);
		case "addpattern":
			return TopicCommands.addPattern(vars);
		case "removepattern":
			return TopicCommands.removePattern(vars);
		case "commands":
			return TopicCommands.adminCommands(vars);
		}

		return null;
	}
	
	public Message parseModerator(String line, Member m, Message message) {
		String[] vars = null;
		if (line.contains(" ")) {
			vars = line.split(" ");
		} else {
			vars = new String[] { line };
		}

		String effective_name;
		if (m == null) effective_name = null;
		else effective_name = m.getEffectiveName();
		
		switch (vars[0].toLowerCase()) {
		case "warn":
			return ModeratorCommands.warn(vars, m);
		case "getwarns":
			return ModeratorCommands.getWarns(vars, effective_name);
		case "warns":
			return ModeratorCommands.getWarns(vars, effective_name);
		case "listwarns":
			return ModeratorCommands.getWarns(vars, effective_name);
		case "removewarn":
			return ModeratorCommands.removeWarn(vars, effective_name);
		case "gatereactrole":
			return ModeratorCommands.gateReactRole(vars, message);
		}

		return null;
	}
}