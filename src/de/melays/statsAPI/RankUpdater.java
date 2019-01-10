package de.melays.statsAPI;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class RankUpdater {
	
	StatsAPI m;
	Channel c;
	
	public RankUpdater(Channel c){
		
		this.m = c.plugin;
		this.c = c;
		
	}
	
	ArrayList<UUID> ranks = new ArrayList<UUID>();
	
	@SuppressWarnings("deprecation")
	public void updateRank (String key){
		System.out.println("[StatsAPI] Starting asynchronous Thread");
		m.getServer().getScheduler().scheduleAsyncDelayedTask(m, new Runnable() {

			public void run() {
				
				try {
					//System.out.println("[StatsAPI] Received a request to update the ranks in the channel ["+c.name+"] related to key ["+key+"]");
					ranks = new ArrayList<UUID>();
					Statement s = m.c.createStatement();
					ResultSet r = s.executeQuery("SELECT * FROM "+ c.name +" ORDER BY "+ key +";");
					while (r.next()){
						ranks.add(UUID.fromString(r.getString("UUID")));
					}
					Collections.reverse(ranks);
					//System.out.println("[StatsAPI] Succesfully updated " + ranks.size() + " ranks");
				
				} catch (Exception e) {

				}
				
			}
			
		}, 0 );
		
	}
	
	public int getRank(UUID u){
		return this.ranks.indexOf(u) + 1;
	}
	
	public UUID getRank(int u){
		return this.ranks.get(u-1);
	}

}
