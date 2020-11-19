package net.peacefulcraft.rtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import net.peacefulcraft.rtp.PCNEssentials;

public class MakeTurkey implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("pcn.staff")) { return true; }

        for(Entity ent : Bukkit.getWorld("world").getEntities()) {
            if(!(ent instanceof Chicken)) { continue; }
            Chicken chick = (Chicken) ent;
            
            if(chick.hasMetadata("Event")) { continue; }

            // Flagging chickens meta data and setting name
            chick.setMetadata("Event", new FixedMetadataValue(PCNEssentials.getPluginInstance(), "Turkey"));
            chick.setCustomName("Thanksgiving Turkey");
            chick.setCustomNameVisible(true);
        }

        sender.sendMessage("Set all chicken entities to Turkey.");
        
        return false;
    }
}
