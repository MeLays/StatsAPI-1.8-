package de.melays.statsAPI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;

public class RuleListener {
	
	StatsAPI plugin;
	
	Channel rules;
	
	ArrayList<Rule> rulelist = new ArrayList<Rule>();
	
	public RuleListener (StatsAPI plugin) {
		this.plugin = plugin;
		this.rules = plugin.rules;
		loadRules();
	}
	
	public void loadRules () {
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		try {
			try {
				Statement s = plugin.c.createStatement();
				ResultSet result = s.executeQuery("SELECT * FROM "+rules.name+";");
				while (result.next()){
					Rule rule = new Rule(plugin);
					rule.setListen(result.getString("listen"));
					rule.setDo(result.getString("do"));
					rule.fetch();
					rulelist.add(rule);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void reportAdd (UUID uuid , String channelname , String key , int amount) {
		for (Rule rule : rulelist) {
			if (rule.key.equalsIgnoreCase(key) && (channelname.equalsIgnoreCase(rule.channel) || rule.channel.equalsIgnoreCase("all"))) {
				rule.execute(uuid , amount);
			}
		}
	}
	
}
