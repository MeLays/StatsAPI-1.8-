package de.melays.statsAPI;

import java.util.UUID;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Rule {
	
	String listen;
	String todo;
	
	String[] dolist;
	
	String channel = "none";
	String key = "none";
	
	StatsAPI plugin;
	
	public Rule(StatsAPI plugin) {
		this.plugin = plugin;
	}
	
	public void setListen(String s) {
		this.listen = s;
	}
	
	public void setDo(String s) {
		this.todo = s;
	}
	
	public void fetch() {
		try {
			key = listen.split(";")[0];
			channel = listen.split(";")[1];
			dolist = todo.split(";");
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void execute(UUID uuid , int amount) {
		for (String s : dolist)
			try {
				String command = s.split("->")[0];
				String other = s.split("->")[1];
				if (command.equals("message")) {
					String message = other;
					message = message.replace("%edit%", amount + "");
					Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				}
				else if (command.equals("add")) {
					String[] fetch = other.split(",");
					String channel = fetch[0];
					String key = fetch[1];
					Channel add = plugin.hookChannel(plugin, channel);
					int old = add.getKey(uuid, key) + amount;
					add.setKey(uuid, key, old);
				}
			}catch (Exception ex) {
				ex.printStackTrace();
			}
	}
}
