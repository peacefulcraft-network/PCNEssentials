package net.peacefulcraft.rtp.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.peacefulcraft.rtp.PCNEssentials;

public class PumpkinMedal implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(!(arg0 instanceof Player)) { return true; }
		if(!arg0.hasPermission("pcn.staff")) { return true; }

		Player p = (Player) arg0;
		if (arg3.length < 3) {
			p.sendMessage(PCNEssentials.messagePrefix + "Please include the medal 'title,' 'placement,' and 'description'.");
			return true;
		}

		String title = arg3[0].replaceAll("_", " ");
		String placement = arg3[1].replaceAll("_", " ");
		String description = arg3[2].replaceAll("_", " ");

		ItemStack pumpkin = new ItemStack(Material.JACK_O_LANTERN, 1);
		ItemMeta pumpkinMeta = pumpkin.getItemMeta();
		pumpkinMeta.setDisplayName(title);
		pumpkinMeta.setLore(new ArrayList<String>(Arrays.asList(placement, description)));
		pumpkin.setItemMeta(pumpkinMeta);

		// If space, add to inv. Otherwise drop it at the player's location
		if (p.getInventory().firstEmpty() == -1) {
			p.getWorld().dropItem(p.getLocation(), pumpkin);
		} else {
			p.getInventory().addItem(pumpkin);
		}
		return true;
	}
}
