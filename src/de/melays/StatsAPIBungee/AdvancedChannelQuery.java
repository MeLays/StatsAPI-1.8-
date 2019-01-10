package de.melays.StatsAPIBungee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdvancedChannelQuery {
	
	Channel c;
	StatsAPIBungee plugin;
	
	public AdvancedChannelQuery (StatsAPIBungee api , Channel c){
		
		this.c = c;
		plugin = api;
		
	}
	
	public ArrayList<String> querryUsersWhere (String key , String equals){
		return querryUsersWhere (key , key , equals);
	}
	
	public ArrayList<String> querryUsersWhere (String get , String key , String equals){
		try {
			if (plugin.c.isClosed()){
				plugin.reconnect();
			}
		} catch (SQLException e1) {

		}
		try {
			ArrayList<String> returnl = new ArrayList<String>();
			if (c.hasKey(key)){
				Statement s;
				try {
					s = plugin.c.createStatement();
					ResultSet result = s.executeQuery("SELECT "+get+" FROM "+c.name+" WHERE "+key+"='"+equals+"';");
					while (result.next()){
						returnl.add(result.getString(get));
					}
					return returnl;
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("[StatsAPI] Couldn't get Key "+key+"!");
				}
			}
			return returnl;
		}catch (Exception ex) {
			return null;
		}
	}

}
