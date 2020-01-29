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
			
			for (Topic topic : TopicLoader.getAllTopics()) {
				boolean restricted = false;
				
				if (topic.channels != null)
				{
					restricted = true;
					
					for (String c : topic.channels)
					{
						if (e.getTextChannel().getId().equals(c))
						{
							restricted = false;
							break;
						}
					}
				}
				
				if (topic.regex != null && restricted == false)
					for (String pattern : topic.regex) {
						if (Pattern.matches(pattern, e.getMessage().getContentRaw().toLowerCase())) {
							e.getTextChannel().sendMessage(Topic.displayTopic(topic, "Triggered by: "+e.getMember().getEffectiveName())).queue(queueSuccess, queueError);
							return;
						}
					}	
				
				boolean invoked = false;
				
				if (e.getMessage().getContentDisplay().toLowerCase().startsWith("!"+topic.topic)) {
					invoked = true;
				} else {
					if (topic.aliases != null)
						for (String t : topic.aliases)
						{
							if (e.getMessage().getContentDisplay().toLowerCase().startsWith("!"+t))
							{
								invoked = true;
								break;
							}
						}
				}
				
				if (invoked) {
					e.getTextChannel().sendMessage(Topic.displayTopic(topic, "Invoked by: "+e.getMember().getEffectiveName())).queue(success -> {
						PrimeLogger.info("<%1> %2", success.getTextChannel().getName(), success.getContentDisplay());
						
						if (e.getGuild().getSelfMember().hasPermission(new Permission[] { Permission.MESSAGE_MANAGE }))
							e.getMessage().delete().complete();
					}, queueError);
					
					return;
				}
			}
		}
	}
	

	/*@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		for (String id : entry_messages)
		{
			if (e.getMessageId().equals(id)) {
				if (e.getReactionEmote().getName().equals(Configuration.gate_emote_id)) {
					Role verified = e.getJDA().getRoleById(Configuration.gate_role_id);
					if (verified == null) {
						PrimeLogger.severe("Couldn't retrieve verified role.");
					}
					
					verified.getGuild().getController().addSingleRoleToMember(verified.getGuild().getMember(e.getUser()), verified).queue(success -> {
						entry_messages.remove(id);
						e.getUser().openPrivateChannel().complete().sendMessage("Thank you. You now have access to the server.\n"
								+ "Please be sure to see the <#385204907054989316> channel and the <#646907687254360074> before posting!"
								+ "\nRemember: **All piracy discussion is against the rules**.").complete();
					}, failure -> {
						failure.printStackTrace();
						e.getTextChannel().sendMessage("There has been an internal error. Please contact <@167445584142139394> or <@249003275976835074>").queue();
						
						e.getGuild().getTextChannelById("562547073459945489")
							.sendMessage("Couldn't give verified role to: `"+e.getMember().getEffectiveName()+"`. Error: "+failure.getMessage()).complete();
					});;
				}
			}
		}
	}*/
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (Configuration.gate_message_id != null)
		{
			if (e.getMessageId().equals(Configuration.gate_message_id)) {
				if (e.getReactionEmote().getName().equals(Configuration.gate_emote_id)) {
					Role verified = e.getGuild().getRoleById(Configuration.gate_role_id);
					if (verified == null) {
						PrimeLogger.severe("Couldn't retrieve verified role.");
					}
					
					e.getGuild().getController().addSingleRoleToMember(e.getMember(), verified).queue(success -> {}, failure -> {
						failure.printStackTrace();
						
						if (Configuration.botlog_channel_id != null)
							e.getGuild().getTextChannelById(Configuration.botlog_channel_id)
								.sendMessage("Couldn't give verified role to: `"+e.getMember().getEffectiveName()+"`. Error: "+failure.getMessage()).complete();
					});;
				}
			}
		}
	}
	
	/*
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e)
	{
		e.getUser().openPrivateChannel().complete()
				.sendMessage("Welcome to the official PrimeHack Discord server."
						+ "\nYou can find the answers to most questions including an install guide, and tips on improving performance/FPS here: https://github.com/shiiion/dolphin/wiki/"
						+ "\nBe sure to read <#646907687254360074> before posting."
						+ "\n**Disclaimer: All piracy discussion including questions related to where you can obtain a copy of Metroid: Prime Trilogy, is against the rules.**").complete();
	}
	*/
	
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
		case "setaliases":
			return TopicCommands.setAliases(vars);
		case "addpattern":
			return TopicCommands.addPattern(vars);
		case "removepattern":
			return TopicCommands.removePattern(vars);
		case "addchannel":
			return TopicCommands.addChannel(vars);
		case "removechannel":
			return TopicCommands.removeChannel(vars);
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