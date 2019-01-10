package de.melays.StatsAPIBungee;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

public class Channel {
	
	StatsAPIBungee plugin;
	String name;
	
	public Channel (StatsAPIBungee p , String name) {
		plugin = p;
		name = name.toUpperCase();
		this.name = name;
		System.out.println("[StatsAPI] Hooking Channel " + name + " ...");
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		if (plugin.isDummy()){
			System.out.println("[StatsAPI] " + ChatColor.RED + "Running in Dummymode. No changes will occure!");
		}
		else{
			try {
				Statement s = plugin.c.createStatement();
				s.execute("CREATE TABLE IF NOT EXISTS " + name + " (UUID VARCHAR(36) UNIQUE)");
				System.out.println("[StatsAPI] Channel succesfully hooked!");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[StatsAPI] Error hooking this Channel!");
			}
			
		}
	}
	
	public boolean hasKey (String key){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		Statement s;
		try {
			s = plugin.c.createStatement();
			ResultSet keys = s.executeQuery("SHOW columns FROM "+ name +";");
			ArrayList<String> keylist = new ArrayList<String>();
			while (keys.next()){
				keylist.add(keys.getString("Field"));
			}
			for (String str : keylist){
				if (str.equalsIgnoreCase(key)){
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[StatsAPI] Created key " + key + "!");
		}
		return false;
	}
	
	public void createKey (String key , boolean str){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		if (!hasKey(key)){
			Statement s;
			try {
				s = plugin.c.createStatement();
				if (str){
					s.execute("ALTER TABLE `"+ name +"` ADD COLUMN `"+ key +"` TEXT");	
				}
				else{
					s.execute("ALTER TABLE `"+ name +"` ADD COLUMN `"+ key +"` INT NOT NULL DEFAULT 0");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[StatsAPI] Created key " + key + "!");
			}
		}
	}
	
	ArrayList<String> created = new ArrayList<String>();
	
	public void setKey (UUID uuid , String key , int to){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		if (!created.contains(key)){
			createKey (key , false);
			created.add (key);
		}
		Statement s;
		try {
			s = plugin.c.createStatement();
			s.execute("INSERT INTO "+ name +" (UUID) VALUES ('"+uuid+"') ON DUPLICATE KEY UPDATE UUID=UUID;");
			s = plugin.c.createStatement();
			s.execute("UPDATE " + name + " "
					+ "SET " + key + "=" + to + " "
					+ "WHERE UUID='"+uuid.toString()+"';");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[StatsAPI] Couldn't create Key!");
		}
	}
	
	public int getKey (UUID uuid , String key){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		if (hasKey(key)){
			Statement s;
			try {
				s = plugin.c.createStatement();
				ResultSet result = s.executeQuery("SELECT "+key+" FROM "+name+" WHERE UUID='"+uuid+"';");
				if (result.next()){
					return result.getInt(key);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[StatsAPI] Couldn't get Key "+key+"!");
			}
		}
		return 0;
	}
	
	public void addToKey (UUID uuid , String key , int incr){
		int old = getKey(uuid , key);
		setKey(uuid , key , old + incr);
	}
	
	public void setStringKey (UUID uuid , String key , String str){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		if (!created.contains(key)){
			createKey (key , true);
			created.add (key);
		}
		Statement s;
		try {
			s = plugin.c.createStatement();
			s.execute("INSERT INTO "+ name +" (UUID) VALUES ('"+uuid+"') ON DUPLICATE KEY UPDATE UUID=UUID;");
			s = plugin.c.createStatement();
			s.execute("UPDATE " + name + " "
					+ "SET " + key + "='" + str + "' "
					+ "WHERE UUID='"+uuid.toString()+"';");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[StatsAPI] Couldn't create Key!");
		}
	}
	
	public AdvancedChannelQuery getAdvancedChannelQuery(){
		return new AdvancedChannelQuery (this.plugin , this);
	}
	
	public String getStringKey (UUID uuid , String key){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		if (hasKey(key)){
			Statement s;
			try {
				s = plugin.c.createStatement();
				ResultSet result = s.executeQuery("SELECT "+key+" FROM "+name+" WHERE UUID='"+uuid+"';");
				result.next();
				return result.getString(key);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("[StatsAPI] Couldn't get Key "+key+"!");
			}
		}
		return null;
	}
	
	public HashMap<String , String> getAllKeys (UUID uuid){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (Exception e1) {

		}
		Statement s;
		try {
			HashMap<String , String> returnmap = new HashMap<String , String>();
			s = plugin.c.createStatement();
			ResultSet result = s.executeQuery("SELECT * FROM "+name+" WHERE UUID='"+uuid+"';");
			ResultSetMetaData rsmd = result.getMetaData();
			int columnCount = rsmd.getColumnCount();
			result.next();
			for (int i = 1; i <= columnCount; i++ ) {
				String name = rsmd.getColumnName(i);
				returnmap.put(name, result.getString(i));
			}
			return returnmap;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[StatsAPI] Failed to get all keys of UUID "+uuid);
		}
		return null;
	}
	
}
