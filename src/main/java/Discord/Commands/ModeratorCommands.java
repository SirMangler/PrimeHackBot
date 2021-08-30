package Discord.Commands;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import Types.WarnMember;
import Utilities.Configuration;
import Utilities.PrimeLogger;
import Utilities.WarnsLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

/**
 * @author SirMangler
 *
 * @date 27 Oct 2019
 */
public class ModeratorCommands {

	static String hammer_url = null;
	public static Role warnrole;
	
	public static Message warn(String[] vars, Member m) {
		if (vars.length < 3) {
			return new MessageBuilder("Syntax: warn [member] [message]").build();
		}	
		
		if (m == null)
			return new MessageBuilder("Cannot find member: "+vars[1]).build();
		
		if (!m.getGuild().getSelfMember().hasPermission(new Permission[] { Permission.MANAGE_ROLES, Permission.BAN_MEMBERS })) {
			return new MessageBuilder("Cannot issue warning. I require the `MANAGE_ROLES` and `BAN_MEMBERS` permission.").build();
		}
		
		if (warnrole == null)
			warnrole = m.getJDA().getRoleById(Configuration.mute_role_id);
		
		String[] warn = new String[vars.length-2];
		for (int i = 2; i < vars.length; i++) {
			warn[i-2] = vars[i];
		}
		
		String warning = String.join(" ", warn);
		
		PrimeLogger.info("Adding warning '%1' to '%2'", warning, m.getEffectiveName());
		int count = WarnsLoader.addWarning(vars[1], warning);
		
		EmbedBuilder b = new EmbedBuilder();
		b.setTitle("Official Warn");
		b.setColor(Color.RED);

		switch (count) {
			case 1: {
				b.setDescription(m.getAsMention()+" Please thoroughly read the #rules channel.");
				b.setFooter("Warning 1", hammer_url);
				break;
			}
			case 2: {
				b.setDescription(m.getAsMention()+" You have been warned and muted for 30 minutes.");
				b.setFooter("Warning 2", hammer_url);
				mute(m, 30);
				
				break;
			}
			case 3: {
				b.setDescription(m.getAsMention()+" You have been warned and muted for 12 hours.\nThe next warning will result in a **ban**.");
				b.setFooter("Warning 3 - Final Warning -", hammer_url);
				mute(m, 720);
				
				break;
			}
			case 4: {
				b.setDescription(m.getAsMention()+" has been banned.");
				b.setFooter("Final warning has been met.", hammer_url);
				m.getGuild().ban(m, 0, "Fourth warning issued by bot command.").complete();
				
				break;
			}
		}
		
		return new MessageBuilder(b).build();
	}
	
	public static Message getWarns(String[] vars, String display_name) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: getWarns [member]").build();
		}
		
		WarnMember m = WarnsLoader.getWarnMember(vars[1]);
		if (m == null) {
			PrimeLogger.info("Cannot find WarnMember: '%1' (%2)", display_name, vars[1]);
			return new MessageBuilder("Cannot find warnings for: "+display_name).build();
		}
		
		StringBuilder b = new StringBuilder();
		if (m.warnings.isEmpty())
			return new MessageBuilder("Member `"+display_name+"` has no warnings!").build();
		
		for (int i = 0; i < m.warnings.size(); i++) {
			b.append((i+1)+". " + m.warnings.get(i) +"\n");
		}
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setDescription(b.toString());
		embed.setTitle("Warnings for: "+display_name);
		embed.setColor(Color.RED);
		
		return new MessageBuilder(embed).build();
	}
	
	public static Message removeWarn(String[] vars, String display_name) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: removeWarn [member] [index]").build();
		}
		
		int index;
		try {
			index = Integer.parseInt(vars[2]);
		} catch (NumberFormatException e) {
			return new MessageBuilder("Unknown number '"+vars[2]+"'").build();
		}
		
		index--; // Displayed list starts from 1
		
		WarnMember m = WarnsLoader.getWarnMember(vars[1]);
		if (m == null) {
			PrimeLogger.info("Cannot find WarnMember: '%1' (%2)", display_name, vars[1]);
			return new MessageBuilder("Cannot find warnings for: "+display_name).build();
		}

		if (m.warnings.isEmpty())
			return new MessageBuilder("Member `"+display_name+"` has no warnings!").build();

		if (m.warnings.size() <= index) {
			return new MessageBuilder("There is no warning index of: " + (index + 1)).build();
		}
		
		WarnsLoader.removeWarning(m.user_id, index);
		
		if (WarnsLoader.getWarnMember(vars[1]).warnings.isEmpty()) {
			return new MessageBuilder("Last warning removed. No more warnings to display.").build();
		}
		
		return getWarns(vars, display_name);
	}
	

	public static Message toDo(String[] vars, String display_name) {
		if (vars.length < 2) {
			return new MessageBuilder("Syntax: todo [idkmijad]").build();
		}
		
		int index;
		try {
			index = Integer.parseInt(vars[2]);
		} catch (NumberFormatException e) {
			return new MessageBuilder("Unknown number '"+vars[2]+"'").build();
		}
		
		WarnMember m = WarnsLoader.getWarnMember(vars[1]);
		if (m == null) {
			PrimeLogger.info("Cannot find WarnMember: '%1' (%2)", display_name, vars[1]);
			return new MessageBuilder("Cannot find warnings for: "+display_name).build();
		}

		if (m.warnings.isEmpty())
			return new MessageBuilder("Member `"+display_name+"` has no warnings!").build();

		WarnsLoader.removeWarning(m.user_id, index-1);
		
		if (WarnsLoader.getWarnMember(vars[1]).warnings.isEmpty()) {
			return new MessageBuilder("Last warning removed. No more warnings to display.").build();
		}
		
		return getWarns(vars, display_name);
	}
	
	public static Message gateReactRole(String[] vars, Message input) {
		if (vars.length < 3)
			return new MessageBuilder("Syntax: gateReactRole [message-id] [@role|role-id] [emote-name|emote-id]").build();
		
		Message m = input.getChannel().retrieveMessageById(vars[1]).complete();
		
		if (m == null) 
			return new MessageBuilder("Cannot find message: "+vars[1]).build();
		
		String role_id;
		if (m.getMentionedRoles().isEmpty()) {
			List<Role> roles = input.getGuild().getRolesByName(vars[2], true);
			
			if (roles.isEmpty()) 
				return new MessageBuilder("Cannot find role: "+vars[2]).build();
			
			role_id = roles.get(0).getId();
		} else {
			role_id = m.getMentionedRoles().get(0).getId();
		}
		
		String emote_id;
		if (m.getEmotes().isEmpty()) {
			List<Emote> emotes = input.getGuild().getEmotesByName(vars[3], true);
			
			if (emotes.isEmpty()) 
				return new MessageBuilder("Cannot find emote: "+vars[3]).build();
			
			emote_id = emotes.get(0).getId();
		} else {
			emote_id = m.getEmotes().get(0).getId();
		}

		Configuration.setGateReact(m.getId(), role_id, emote_id);
		m.addReaction(m.getGuild().getEmoteById(emote_id)).complete();
		
		return new MessageBuilder("The settings have been updated.").build();
	}
	
	public static LinkedHashMap<Member, Long> muted = new LinkedHashMap<Member, Long>();
	private static Thread warnclock;
	public static void mute(Member m, int durationMinute) {
		muted.put(m, System.currentTimeMillis() + (durationMinute * 60000));
		m.getGuild().addRoleToMember(m, warnrole).complete();
		
		if (warnclock == null || !warnclock.isAlive()) {
			warnclock = new Thread(new WarnClock());
			warnclock.start();
		}
	}
}

class WarnClock implements Runnable {
	
		@Override
		public void run() {
			while (true) {
				if (ModeratorCommands.muted.isEmpty())
					return;
				
				long time = System.currentTimeMillis();
				
				for (Entry<Member, Long> entry : ModeratorCommands.muted.entrySet()) {
					if (time > entry.getValue()) {
						entry.getKey()
							.getGuild()
							.removeRoleFromMember(entry.getKey(), ModeratorCommands.warnrole).complete();
						
						ModeratorCommands.muted.remove(entry.getKey());
					}
				}
				
				try {
					Thread.sleep(150000); // 2.5 minutes
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}
}