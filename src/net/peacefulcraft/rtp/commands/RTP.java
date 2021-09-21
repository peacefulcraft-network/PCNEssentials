package net.peacefulcraft.rtp.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;
import net.peacefulcraft.rtp.configuration.RTPRadiusLimit;

public class RTP implements CommandExecutor{

	private HashMap<UUID, Long> usage;
	
	public RTP(FileConfiguration c) {
		this.usage = new HashMap<UUID, Long>();
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		HashMap<String, RTPRadiusLimit> ranges = Configuration.getRtpRanges();

		if (!Configuration.getRtpEnabled()) {
			arg0.sendMessage(PCNEssentials.messagePrefix + "RTP Command is disabled.");
			return true;
		}

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
				x = (int) Math.round(Math.random() * limit.getMaxRadius());
				z = (int) Math.round(limit.getMinRadius() + (Math.random() * (limit.getMaxRadius() - limit.getMinRadius())));
			} else {
				x = (int) Math.round(limit.getMinRadius() + (Math.random() * (limit.getMaxRadius() - limit.getMaxRadius())));
				z = (int) Math.round(Math.random() * limit.getMaxRadius());
			}
			
			if(Math.random() > 0.5) {
				x *= -1;
			}
			
			
			if(Math.random() > 0.5) {
				z *= -1;
			}
			
			p.sendMessage(ChatColor.BLUE + "You've been teleported to (" + x + ", " + y + ", " + z + ")");
			PCNEssentials.getPluginInstance().logNotice(p.getName() + " Teleported to " + x + ", " + y + ", " + z);
			
			int resistanceDurationTicks = Configuration.getRtpResistanceDuration() * 20;
			if (Configuration.getRtpResistanceDuration() == 0) {
				PCNEssentials.getPluginInstance().logNotice("No rtp.resistance_duration value was found in the config. Defaulting to 10 seconds.");
				resistanceDurationTicks = 200;
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, resistanceDurationTicks, 255));
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
}
