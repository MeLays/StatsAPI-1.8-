package de.melays.statsAPI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{
	
	StatsAPI api;
	Channel namedb;
	
	public EventListener (StatsAPI api){
		
		namedb = api.hookChannel(api, "STATSAPI_NAMEDB");
		this.api = api;
		
	}
	
	@EventHandler
	public void onJoin (PlayerJoinEvent e){
		System.out.println("[StatsAPI] Updated " + e.getPlayer().getName() + " in the Database!");
		namedb.setStringKey(e.getPlayer().getUniqueId(), "NAME" , e.getPlayer().getName());
		api.namecache.put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
	}

}
