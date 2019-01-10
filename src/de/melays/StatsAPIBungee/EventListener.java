package de.melays.StatsAPIBungee;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener{
	
	StatsAPIBungee api;
	Channel namedb;
	
	public EventListener (StatsAPIBungee api){
		
		namedb = api.hookChannel(api, "STATSAPI_NAMEDB");
		this.api = api;
		
	}
	
	@EventHandler
	public void onJoin (PostLoginEvent e){
		System.out.println("[StatsAPI] Updated " + e.getPlayer().getName() + " in the Database!");
		namedb.setStringKey(e.getPlayer().getUniqueId(), "NAME" , e.getPlayer().getName());
		api.namecache.put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
	}

}
