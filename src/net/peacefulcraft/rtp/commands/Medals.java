package net.peacefulcraft.rtp.commands;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Medals implements CommandExecutor {
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if(!(arg0 instanceof Player)) { return true; }
        if(!arg0.hasPermission("pcn.staff")) { return true; }

        Player p = (Player) arg0;
        if(arg3[1].isEmpty() || arg3[2].isEmpty()) {
            p.sendMessage("Command usage: /medals [place][usage][date]");
            return true;
        }

        String placement = null;
        if(arg3[0].equalsIgnoreCase("first")) {
            placement = "First";
        } else if(arg3[0].equalsIgnoreCase("second")) {
            placement = "Second";
        } else if(arg3[0].equalsIgnoreCase("third")) {
            placement = "Third";
        } else {
            p.sendMessage("Invalid placement. Valid options are: first, second, third.");
            return true;
        }
        
        ItemStack third = new ItemStack(Material.IRON_INGOT, 1);
        ItemMeta meta = third.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + placement + " Place Medal");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GREEN + "Official PCN " + arg3[1] + " Medal");
        lore.add(ChatColor.GREEN + arg3[2]);

        third.setItemMeta(meta);

        third.addEnchantment(Enchantment.DEPTH_STRIDER, 15);
        
        p.getInventory().addItem(third);
        return true;
    }
 }