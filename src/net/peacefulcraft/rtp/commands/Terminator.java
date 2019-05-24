package net.peacefulcraft.rtp.commands;

import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.peacefulcraft.rtp.tasks.Death;

public class Terminator implements CommandExecutor{

	public static boolean executing = false;
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if( (arg0 instanceof Player) || (arg0 instanceof ConsoleCommandSender)){
			
			if(arg1.getName().equalsIgnoreCase("terminator")){
			
				if(arg0.hasPermission("pcn.staff")) {
			
					arg0.sendMessage("Report:");
					for(Entry<EntityType, Integer> e : Death.getKills().entrySet()) {
						
						arg0.sendMessage( e.getKey() + " : " + e.getValue() + " killed");
						
					}
					return true;		
				}
			}		
		}
		
		return false;
	}

}
