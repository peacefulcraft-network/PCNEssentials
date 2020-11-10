package net.peacefulcraft.rtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.configuration.Configuration;

public class Crusade implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Configuration.getCrusadeEnabled()) { return true; }
        //if(!sender.hasPermission("pcn.staff")) { return true; }

        try{
            if(args[0].isEmpty()) {
                sender.sendMessage("Command usage: /crusade");
                return true;
            }
        } catch(IndexOutOfBoundsException ex) {
            sender.sendMessage("Command usage: /crusade");
            return true;
        }

        // Fetching player target
        Player p = Bukkit.getPlayer(args[0]);
        if(p == null) {
            sender.sendMessage("Player is not found.");
            return true;
        }
        
        //p.getWorld().createExplosion(p.getLocation(), 100, false, false);
        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 2));
        p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10, 1);
        p.sendMessage(ChatColor.DARK_AQUA + "EMBRACE THE CRUSADE.");

        // Throwing players
        Location loc = p.getLocation();
        loc.setPitch(0);
        Vector vec = loc.getDirection();
        p.setVelocity(vec.multiply(5));

        if(sender instanceof Player) {
            Player t = (Player)sender;
            t.playSound(t.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10, 1);
        }

        return false;
    }
    
}