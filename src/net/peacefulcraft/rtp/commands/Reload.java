package net.peacefulcraft.rtp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.PCNEssentials;

/**
 * Reload
 */
public class Reload implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("pcn.staff")) {
            PCNEssentials.getPluginInstance().getCommand("rtp").setExecutor(
                new RTP(PCNEssentials.getPluginInstance().getConfig())
            );

            sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.BLUE + "RTP Ranges Reloaded.");
        }

        return true;
	}
}