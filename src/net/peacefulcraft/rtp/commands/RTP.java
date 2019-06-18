package net.peacefulcraft.rtp.commands;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.peacefulcraft.rtp.PCNEssentials;

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
		
		if(p.getLocation().getWorld().getEnvironment().equals(Environment.NETHER)) {
			p.sendMessage(ChatColor.BLUE + "Sorry, you can't RTP in the Nether.");
			return true;
		}
		
		if(p.getLocation().getWorld().getEnvironment().equals(Environment.THE_END)) {
			p.sendMessage(ChatColor.BLUE + "Sorry, you can't RTP in the End.");
			return true;
		}
		
		if(usage.get(p.getUniqueId()) == null || (System.currentTimeMillis() - usage.get(p.getUniqueId())) > 60000) {
			
			int x, y, z;
			x = (int) Math.round(3000 + (Math.random() * 7000));
			y = 250;
			z = (int) Math.round(3000 + (Math.random() * 7000));
			
			if(Math.random() > 0.5) {
				x *= -1;
			}
			
			
			if(Math.random() > 0.5) {
				z *= -1;
			}
			
			p.sendMessage(ChatColor.BLUE + "You've been teleported to (" + x + ", " + y + ", " + z + ")");
			PCNEssentials.getPluginInstance().getLogger().log(Level.INFO, p.getName() + " Teleported to " + x + ", " + y + ", " + z);
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2000, 255));
			p.teleport(new Location(p.getWorld(), x, y, z));
			
			usage.put(p.getUniqueId(), System.currentTimeMillis());
			
			return true;
			
		}else {
			p.sendMessage(ChatColor.RED + "Sorry, you're on cool down for " + 
					((System.currentTimeMillis() - usage.get(p.getUniqueId())) / 1000) + " seconds.");
			return false;
		}
	}
	
}