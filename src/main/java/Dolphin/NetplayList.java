package Dolphin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import Utilities.PrimeLogger;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SirMangler
 *
 * @date 5 Jun 2020
 */
public class NetplayList {

	OkHttpClient client;
	private final List<String> ids = new ArrayList<String>();
	private final List<String> session_strs = new ArrayList<String>();
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	public NetplayList()
	{
		PrimeLogger.info("Starting NetplayList poller");
		
		client = new OkHttpClient.Builder()
			    .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
			    .build();
		
		ScheduledExecutorService scex = Executors.newScheduledThreadPool(2);
		scex.scheduleWithFixedDelay(() -> clearIDS(), 2, 2, TimeUnit.HOURS);
		scex.scheduleWithFixedDelay(() -> updateSessionsChannel(), 0, 2, TimeUnit.MINUTES);
	}
	
	public void clearIDS()
	{
		ids.clear();
		session_strs.clear();
	}
	
	public void updateSessionsChannel() {
		PrimeLogger.debug("Gathering Netplay session list");
		
		Queue<String> sessions = getSessionList();
		
		if (sessions.size() > 9)
		{
			String[] ses = new String[9];
			int i = 0;
			while (!sessions.isEmpty()) 
			{
				if (i == 10)
				{
					sendEmbeds(createEmbeds(ses));
					
					i = 0;
					ses = new String[9];
				}
				
				ses[i] = sessions.poll();
				i++;
			}
			
			sendEmbeds(createEmbeds(ses));
		} else {
			String[] ses = sessions.toArray(new String[0]);

			sendEmbeds(createEmbeds(ses));
		}
	}
	
	public void sendEmbeds(JSONArray embeds)
	{
		JSONObject obj = new JSONObject();
		obj.put("embeds", embeds);
		
		RequestBody body = RequestBody.create(JSON, obj.toString());
		Request req = new Request.Builder().url("https://discordapp.com/api/webhooks/718439267893903422/8RhWloyKGEC0-sHUyMicttqABeA55C2JTc2fB0xVJ-c1d_MkqtaHZRGUZI3VAdqOUhov").post(body).build();
		
		try {
			Response r = client.newCall(req).execute();
			
			r.body().close();
		} catch (IOException e) {
			PrimeLogger.severe("[NetplayList] Failed to send embeds to Discord.");
			e.printStackTrace();
		}
	}
	
	public JSONArray createEmbeds(String[] sessions)
	{
		JSONArray arr = new JSONArray();
	
		for (String session : sessions)
		{
			if (session == null)
				continue;
			
			if (session.length() >= 1950)
				session = "Content too large to display.";
			
			JSONObject embed = new JSONObject();
			//embed.put("title", "New Session");
			embed.put("description", session);
			embed.put("color", 0x17ad00);
			
			arr.put(embed);
		}
		
		return arr;
	}
	
	public LinkedList<String> getSessionList()
	{
		Request req = new Request.Builder()
				.url("https://lobby.dolphin-emu.org/v0/list")
				.get()
				.build();

		String b;
		try {
			Response r = client.newCall(req).execute();
			b = r.body().string();
			
			if (r.code() < 200 || r.code() > 220) {
				PrimeLogger.severe("Received Status Code: " + r.code());
				return null;
			}
			
			r.close();
		} catch (IOException e) {
			PrimeLogger.severe("Failed to gather session data from Dolphin!");
			PrimeLogger.severe(e.getMessage());
			e.printStackTrace();
			
			return null;
		}
		
		JSONObject obj = new JSONObject(b);
		
		if (!obj.has("sessions"))
			return null;
		
		return parseSessionData(obj);
	}
	
	public LinkedList<String> parseSessionData(JSONObject obj)
	{
		JSONArray sessions = obj.getJSONArray("sessions");
		
		LinkedList<String> sessionlist = new LinkedList<String>();
		
		for (int i = 0; i < sessions.length(); i++)
		{
			JSONObject session = sessions.getJSONObject(i);
			
			if (session.getBoolean("password"))
				continue;
			
			if (ids.contains(session.getString("server_id")))
				continue;
			
			ids.add(session.getString("server_id"));
			
			String name = session.getString("name");
			name = name.replaceAll("(?i)nigger", "******");
			name = name.replace("(?i)faggot", "******");
			name = name.replace("(?i)nigga", "*****");
			name = name.replace("(?i)cunt", "*****");
			name = name.replace("(?i)fag", "***");
			
			StringBuilder builder = new StringBuilder();
			builder.append(regionToEmote(session.getString("region")) + " ");
			builder.append("*"+session.getString("name") + "* playing **"+session.getString("game")+"**");
			builder.append(" on version `"+session.getString("version")+"`");
			
			if (session_strs.contains(builder.toString()))
				continue;
			
			session_strs.add(builder.toString());
			sessionlist.add(builder.toString());
		}
		
		return sessionlist;
	}
	
	public String regionToEmote(String code) 
	{
	    switch (code.toUpperCase())
	    {
		    case "EA":
		    	return "🌏";
		    case "CN":
		    	return "🇨🇳";
		    case "EU":
		    	return "🇪🇺";
		    case "NA":
		    	return "🇺🇸";
		    case "SA":
		    	return "🌎";
		    case "OC":
		    	return "🇦🇺";
		    case "AF":
		    	return "🌍";
	    }

	    return code;
	}

}
