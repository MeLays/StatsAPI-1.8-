package de.melays.StatsAPIBungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.io.ByteStreams;
import com.huskehhh.mysql.mysql.MySQL;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class StatsAPIBungee extends Plugin{
	
	MySQL mysql = null;	
	Connection c = null;
	boolean dummy = false;
	
	Channel namedb;
	
	public static StatsAPIBungee getBungeeCordInstance(){
		return (StatsAPIBungee) BungeeCord.getInstance().getPluginManager().getPlugin("StatsAPI");
	}
	
    private Configuration config;

    @Override
    public void onDisable() {

    }

    private boolean loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();

                try (InputStream in = getResourceAsStream("config.yml");
                     OutputStream out = new FileOutputStream(file)) {
                    ByteStreams.copy(in, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Configuration getConfig() {
        return config;
    }
	
	
	public void onEnable(){
		
		loadConfig();
		mysql = new MySQL (getConfig().getString("mysql.host"), getConfig().getString("mysql.port") , getConfig().getString("mysql.database") , getConfig().getString("mysql.user") , getConfig().getString("mysql.password"));
		
		System.out.println("[StatsAPI] Trying to connect to the Database ...");
		reconnect();
		BungeeCord.getInstance().getPluginManager().registerListener(this, new EventListener(this));
		namedb = hookChannel(this , "STATSAPI_NAMEDB");
	
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
	
	public Connection getConnection() {
		try {
			if (c.isClosed()){
				reconnect();
			}
		} catch (SQLException e1) {

		}
		return c;
	}
	
	HashMap <String , ArrayList<String>> hooks = new HashMap <String , ArrayList<String>>();
	
	public Channel hookChannel (Plugin p , String str){
		if (p != this){
			System.out.println("[StatsAPI] Plugin " + p.getDescription().getName() + " hooked into Channel " + str);
			if (!hooks.containsKey(p.getDescription().getName())){
				hooks.put(p.getDescription().getName(), new ArrayList<String> ());
			}
			if (!hooks.get(p.getDescription().getName()).contains(str)){
				hooks.get(p.getDescription().getName()).add(str);
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
	
	public String getUUIDFromName (String playername){
		AdvancedChannelQuery acq = namedb.getAdvancedChannelQuery();
		try {
			return acq.querryUsersWhere("UUID","NAME", playername).get(0);
		} catch (Exception e) {
			return null;
		}
	}

}