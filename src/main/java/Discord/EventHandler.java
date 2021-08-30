package Discord;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import Discord.Commands.ModeratorCommands;
import Discord.Commands.ReadTheWiki;
import Discord.Commands.TopicCommands;
import Types.Topic;
import Types.Tuple;
import Utilities.Configuration;
import Utilities.PrimeLogger;
import Utilities.TopicLoader;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
		
		boolean canTalk = true;
		
		if (e.getChannelType() == ChannelType.TEXT)
			canTalk = e.getTextChannel().canTalk();
		
		if (canTalk) {	
			if (e.getAuthor().getId().equalsIgnoreCase("167445584142139394")) {
				if (e.getMessage().getContentRaw().equalsIgnoreCase("!rantthewiki")) {
					ReadTheWiki.sendRant(e.getTextChannel());
					e.getMessage().delete().queue();
					return;
				}
			}
			
			if (e.getMessage().getContentRaw().startsWith("!")) {
				if (e.getChannelType() == ChannelType.TEXT) {
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
								e.getChannel().sendMessage(message).queue(queueSuccess, queueError);
								return;
							}
						}
					}
				}			
				
				if (e.getMessage().getContentRaw().startsWith("!isthisa "))
				{
					String content = e.getMessage().getContentRaw();
					if (content.length() > 12 && content.contains(" ") && content.contains("|"))
					{
						String[] vars = e.getMessage().getContentRaw().substring(9).split("\\|");
						
						File f = isThisA(vars[0].trim(), vars[1].trim());
						if (f != null)
							e.getChannel().sendFile(f).queue();					
					}
				}
				
				boolean invoked = false;
				
				for (Entry<String, Topic> entry : TopicLoader.getAllAliases().descendingMap().entrySet()) {
					String raw_msg = e.getMessage().getContentRaw();
					int size_difference = raw_msg.length() - entry.getKey().length();
					if (size_difference <= 2 && size_difference >= -2) {
						if (e.getMessage().getContentRaw().startsWith("!"+entry.getKey())) {
							invoked = true;
						}
					}
					
					if (invoked) {
						e.getChannel().sendMessage(Topic.displayTopic(entry.getValue(), "Invoked by: "+e.getMember().getEffectiveName())).queue(success -> {
							PrimeLogger.info("<%1> %2", success.getChannel().getName(), success.getContentDisplay());
							
							if (e.getGuild().getSelfMember().hasPermission(new Permission[] { Permission.MESSAGE_MANAGE }))
								e.getMessage().delete().queue();
						}, queueError);

						return;
					}
				}
			}
			
			for (Topic topic : TopicLoader.getAllAliases().values()) {
				boolean restricted = false;
				
				if (topic.channels != null)
				{
					restricted = true;
					
					for (String c : topic.channels)
					{
						if (e.getChannel().getId().equals(c))
						{
							restricted = false;
							break;
						}
					}
				}
				
				if (topic.regex != null && restricted == false)
					for (String pattern : topic.regex) {
						if (Pattern.matches(pattern, e.getMessage().getContentRaw().toLowerCase())) {
							e.getChannel().sendMessage(Topic.displayTopic(topic, "Triggered by: "+e.getMember().getEffectiveName())).queue(queueSuccess, queueError);
							return;
						}
					}	
			}
		}
	}
	
	HashMap<String, Tuple<Integer>> invokers = new HashMap<String, Tuple<Integer>>();
	Thread invoker_thr;
	public boolean checkInvokeCount(String id)
	{
		if (!invokers.containsKey(id))
			invokers.put(id, new Tuple<Integer>(0, 0));
		
		if (invoker_thr == null || !invoker_thr.isAlive()) 
		{
			invoker_thr = new Thread(() -> {
				while (!invokers.isEmpty())
				{
				
				}
			});
			
			invoker_thr.start();
		}
		
		return true;
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
					
					e.getGuild().addRoleToMember(e.getMember(), verified).queue(success -> {}, failure -> {
						failure.printStackTrace();
						
						if (Configuration.botlog_channel_id != null)
							e.getGuild().getTextChannelById(Configuration.botlog_channel_id)
								.sendMessage("Couldn't give verified role to: `"+e.getMember().getEffectiveName()+"`. Error: "+failure.getMessage()).queue();
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
	
	public File isThisA(String pig_str, String sub_str)
	{
        BufferedImage bufferedImage = null;
        
		try {
			bufferedImage = ImageIO.read(new File("pigeon.png"));
		} catch (IOException e1) {			
			e1.printStackTrace();
			return null;
		}
		
        Graphics graphics = bufferedImage.getGraphics();
        Font f = new Font("Arial Black", Font.BOLD, 25);

        Graphics2D g2 = (Graphics2D) graphics;

        Color originalColor = g2.getColor();
        Stroke originalStroke = g2.getStroke();
        RenderingHints originalHints = g2.getRenderingHints();

        FontRenderContext frc = new FontRenderContext(null,true,true);
        
        GlyphVector glyphVector = f.createGlyphVector(frc, pig_str);
        Shape pigeon = glyphVector.getOutline();
        
        glyphVector = new Font("Lato Regular", Font.PLAIN, 30).createGlyphVector(frc, sub_str);
        Shape subtitle = glyphVector.getOutline();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        AffineTransform t1 = new AffineTransform();
        t1.translate((1100 - pigeon.getBounds().getWidth()) / 2, 90);
        g2.setTransform(t1);
        
        g2.setColor(Color.BLACK);
        g2.draw(pigeon);

        g2.setColor(Color.WHITE);
        g2.fill(pigeon);

        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        
        AffineTransform t2 = new AffineTransform();
        t2.translate((700 - subtitle.getBounds().getWidth()) / 2, 400);
        g2.setTransform(t2);
        
        g2.setColor(Color.BLACK);
        g2.draw(subtitle);

        g2.setColor(Color.WHITE);
        g2.fill(subtitle);

        g2.setColor(originalColor);
        g2.setStroke(originalStroke);
        g2.setRenderingHints(originalHints);
        
        File out = null;
        try {
        	out = new File("output.png");

			ImageIO.write(bufferedImage, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return out;
	}
}