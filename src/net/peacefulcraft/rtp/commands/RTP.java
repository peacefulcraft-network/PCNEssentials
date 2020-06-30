package net.peacefulcraft.rtp.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.peacefulcraft.rtp.PCNEssentials;

public class RTP implements CommandExecutor{

	private HashMap<String, RTPRadiusLimit> ranges;
	private HashMap<UUID, Long> usage;
	
	public RTP(FileConfiguration c) {
		this.usage = new HashMap<UUID, Long>();
		this.ranges = new HashMap<String, RTPRadiusLimit>();

		if (c.getConfigurationSection("rtp").contains("ranges")) {
			for(String range : c.getConfigurationSection("rtp.ranges").getKeys(false)) {
				ConfigurationSection cfgs = c.getConfigurationSection("rtp.ranges." + range);
				if(!cfgs.contains("min")) { continue; }
				if(!cfgs.contains("max")) { continue; }

				int min = cfgs.getInt("min");
				int max = cfgs.getInt("max");

				ranges.put(range, new RTPRadiusLimit(min, max));
				PCNEssentials.getPluginInstance().logNotice("Registered RTP Range " + range + " [" + min + ", " + max + "]");
			}
		}

		PCNEssentials.getPluginInstance().logNotice(ranges.size() + " RTP ranges loaded");
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(ranges.size() < 1) {
			arg0.sendMessage(ChatColor.BLUE + "[" + ChatColor.GREEN  + "PCN" + ChatColor.BLUE + "] " + ChatColor.RESET + "No RTP ranges configured.");
			return true;
		}
		
		if(!(arg0 instanceof Player)) {
			return true;
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

		String range = "";
		for(String name : ranges.keySet()) {
			range = name;
			break;
		}
		if(arg3.length > 0) {
			if(ranges.containsKey(arg3[0])) {
				range = arg3[0];
			} else {
				arg0.sendMessage(ChatColor.BLUE + "[" + ChatColor.GREEN  + "PCN" + ChatColor.BLUE + "] " + ChatColor.RESET + " Invalid RTP range. Valid ranges are:");
				for(String name : ranges.keySet()) {
					arg0.sendMessage(ChatColor.GOLD + name);
				}
				return true;
			}
		}
		
		if(usage.get(p.getUniqueId()) == null || (System.currentTimeMillis() - usage.get(p.getUniqueId())) > 60000) {
			
			int x = 0, y = 250, z = 0;
			RTPRadiusLimit limit = ranges.get(range);

			if(Math.random() > 0.5) {
				x = (int) Math.round(Math.random() * (limit.maxRadius - limit.minRadius));
				z = (int) Math.round(limit.minRadius + (Math.random() * (limit.maxRadius - limit.minRadius)));
			} else {
				x = (int) Math.round(limit.minRadius + (Math.random() * (limit.maxRadius - limit.minRadius)));
				x = (int) Math.round(Math.random() * (limit.maxRadius - limit.minRadius));
			}
			
			if(Math.random() > 0.5) {
				x *= -1;
			}
			
			
			if(Math.random() > 0.5) {
				z *= -1;
			}
			
			p.sendMessage(ChatColor.BLUE + "You've been teleported to (" + x + ", " + y + ", " + z + ")");
			PCNEssentials.getPluginInstance().logNotice(p.getName() + " Teleported to " + x + ", " + y + ", " + z);
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
			p.teleport(new Location(p.getWorld(), x, y, z));
			//PaperLib.teleportAsync(p, new Location(p.getWorld(), x, y, z));
			
			usage.put(p.getUniqueId(), System.currentTimeMillis());
			
			return true;
			
		}else {
			p.sendMessage(ChatColor.RED + "Sorry, you're on cool down for " + 
					(60 - ((System.currentTimeMillis() - usage.get(p.getUniqueId())) / 1000)) + " seconds.");
			return true;
		}
	}
	
	public class RTPRadiusLimit {
		private int minRadius;
		private int maxRadius;

		public RTPRadiusLimit(int minRadius, int maxRadius) {
			this.minRadius = minRadius;
			this.maxRadius = maxRadius;
		}
	}
}
