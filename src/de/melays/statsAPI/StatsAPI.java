package de.melays.statsAPI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.mysql.mysql.MySQL;

public class StatsAPI extends JavaPlugin{
	
	MySQL mysql = null;	
	Connection c = null;
	boolean dummy = false;
	
	Channel namedb;
	Channel rules;
	
	RuleListener ruleListener;
	
	public static StatsAPI getSpigotInstance(){
		return (StatsAPI) Bukkit.getPluginManager().getPlugin("StatsAPI");
	}
	
	public void onEnable(){
		getCommand("statsapi").setExecutor(new Commands(this));
		
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("mysql.host", "127.0.0.1");
		getConfig().addDefault("mysql.port", "3306");
		getConfig().addDefault("mysql.user", "root");
		getConfig().addDefault("mysql.database", "stats");
		getConfig().addDefault("mysql.password", "123");
		saveConfig();
		mysql = new MySQL (getConfig().getString("mysql.host"), getConfig().getString("mysql.port") , getConfig().getString("mysql.database") , getConfig().getString("mysql.user") , getConfig().getString("mysql.password"));
		
		System.out.println("[StatsAPI] Trying to connect to the Database ...");
		reconnect();
		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
		namedb = hookChannel(this , "STATSAPI_NAMEDB");
		rules = hookChannel(this , "STATSAPI_RULES");
		
		ruleListener = new RuleListener(this);
	}
	
	public Connection getConnection() {
		try {
			if (c.isClosed()){
				reconnect();
			}
		} catch (SQLException e1) {

		}
		return c;
	}
	
	public boolean isDummy(){
		return dummy;
	}
	
	public void reconnect(){
		try{
			c = mysql.openConnection();
		}
		catch (Exception ex){
			ex.printStackTrace();
			dummy = true;
			System.out.println("[StatsAPI] Error whilst connecting to the Database. Running in Dummymode!");
		}
	}
	
	HashMap <String , ArrayList<String>> hooks = new HashMap <String , ArrayList<String>>();
	
	public Channel hookChannel (Plugin p , String str){
		if (p != this){
			if (!hooks.containsKey(p.getName())){
				hooks.put(p.getName(), new ArrayList<String> ());
			}
			if (!hooks.get(p.getName()).contains(str)){
				hooks.get(p.getName()).add(str);
				System.out.println("[StatsAPI] Plugin " + p.getName() + " hooked into Channel " + str);
			}
		}
		Channel ch = new Channel(this , str);
		return ch;
	}
	
	HashMap<UUID , String> namecache = new HashMap<UUID , String>();
	
	public String getNameFromUUID (UUID s){
		if (!namecache.containsKey(s)){
			namecache.put(s, namedb.getStringKey(s, "NAME"));
		}
		return namecache.get(s);
	}
	
	public String getUUIDFromName (String s){
		AdvancedChannelQuery acq = namedb.getAdvancedChannelQuery();
		try {
			return acq.querryUsersWhere("UUID","NAME", s).get(0);
		} catch (Exception e) {
			return null;
		}
	}

}