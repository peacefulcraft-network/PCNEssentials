package net.peacefulcraft.rtp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVision implements CommandExecutor{

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(!(arg0 instanceof Player)) {
			return false;
		}
		
		if(!(arg1.getName().equalsIgnoreCase("nv"))) {
			return false;
		}
		
		Player p = (Player) arg0;
		
		if(p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}else {
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1));
		}
		
		return true;
	}

}
