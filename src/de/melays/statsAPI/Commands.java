package de.melays.statsAPI;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {
	
	StatsAPI api;
	
	public Commands (StatsAPI api){
		this.api = api;
	}
	
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        
    	if (!sender.hasPermission("statsapi.info")){
    		sender.sendMessage("Unknown command. Type \"/help\" for help.");
    		return true;
    	}
    	
    	sender.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "[STATSAPI]" + ChatColor.RESET + " Coded by MeLays [ Version " + api.getDescription().getVersion() + " ]");
    	for (String s : api.hooks.keySet()){
    		sendHookerMessage (sender , s);
    	}
    	
        return true;
    }
    
    public void sendHookerMessage (CommandSender sender , String p){
    	ArrayList<String> hooks = api.hooks.get(p);
    	String result = "";
    	for (String s : hooks){
    		if (hooks.indexOf(s) == hooks.size()-1){
    			result += s;
    			break;
    		}
    		result += s + ", ";
    	}
    	sender.sendMessage(ChatColor.GREEN + p + ChatColor.RESET + ": " + result);
    }
	
}
