package net.peacefulcraft.rtp.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RTP implements CommandExecutor{

	private HashMap<UUID, Long> usage = new HashMap<UUID, Long>();
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(!(arg0 instanceof Player)) {
			return false;
		}
		
		if(!(arg1.getName().equalsIgnoreCase("rtp"))) {
			return false;
		}
		
		Player p = (Player) arg0;
		if(usage.get(p.getUniqueId()) == null || (System.currentTimeMillis() - usage.get(p.getUniqueId())) > 60000) {
		
			double x, y, z;
			x = 18000 + (Math.random() * 7000);
			y = 250;
			z = 18000 + (Math.random() * 7000);
			
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2000, 255));
			p.teleport(new Location(p.getWorld(), x, y, z));
			
			usage.put(p.getUniqueId(), System.currentTimeMillis());
			
			p.sendMessage(ChatColor.BLUE + "You've been teleported to (" + x + "," + y + "," + z + ")");
		
			return true;
		}else {
			p.sendMessage(ChatColor.RED + "Sorry, you're on cool down for " + 
					((System.currentTimeMillis() - usage.get(p.getUniqueId())) / 1000) + " seconds.");
			return false;
		}
	}
	
}