package net.peacefulcraft.rtp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.peacefulcraft.rtp.PCNEssentials;

public class ToggleDrops implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("pcn.staff")) { return true; }

        try{
            if(args[0].isEmpty()) {
                sender.sendMessage("Command usage: /randomDrops [enable/disable]");
                return true;
            }
        } catch(IndexOutOfBoundsException ex) {
            sender.sendMessage("Command usage: /randomDrops [enable/disable]");
            return true;
        }

        if(args[0].equalsIgnoreCase("enable")) {
            PCNEssentials.setRandomDrops(true);
            sender.sendMessage("Random Drops enabled.");
            return true;
        }
        if(args[0].equalsIgnoreCase("disable")) {
            PCNEssentials.setRandomDrops(false);
            sender.sendMessage("Random Drops disabled.");
            return true;
        }

        return false;
    }

}
