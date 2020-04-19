package net.peacefulcraft.rtp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.PCNEssentials;

public class Reload implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("pcn.staff")) {
            PCNEssentials.getPluginInstance().reloadConfig();
            FileConfiguration c = PCNEssentials.getPluginInstance().getConfig();

            if (c.getBoolean("rtp.enabled")) {
                PCNEssentials.getPluginInstance().getCommand("rtp").setExecutor(
                    new RTP(PCNEssentials.getPluginInstance().getConfig())
                );
                PCNEssentials.getPluginInstance().logNotice("RTP: Enabled");
            }

            if (c.getBoolean("nv.enabled")) {
                PCNEssentials.getPluginInstance().getCommand("nv").setExecutor(new NightVision());
                PCNEssentials.getPluginInstance().logNotice("NV: Enabled");
            }

            sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.BLUE + "RTP Ranges Reloaded.");
        }

        return true;
	}
}