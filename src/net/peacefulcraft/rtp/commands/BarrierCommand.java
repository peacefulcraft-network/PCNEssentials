package net.peacefulcraft.rtp.commands;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.peacefulcraft.rtp.PCNEssentials;

public class BarrierCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!PCNEssentials.getPluginConfig().getBarrierEnabled()) {
			sender.sendMessage("/barrier command is disabled in plugin configuration.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players are able to execute this command.");
			return true;
		}

		if (!sender.hasPermission("pcn.barrier")) {
			sender.sendMessage("You do not have permission to execute this command.");
			return true;
		}

		Player p = (Player) sender;
		if (!(p.getGameMode() == GameMode.CREATIVE)) {
			sender.sendMessage("Executor must be in creative mode to use this command.");
			return true;
		}

		p.getInventory().addItem(new ItemStack(Material.BARRIER, 1));
		sender.sendMessage("One barrier block added to your inventory!");
		return true;
 	}
	
}
