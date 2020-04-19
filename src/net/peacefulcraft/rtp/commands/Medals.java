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
        try{
            if(arg3[1].isEmpty() || arg3[2].isEmpty()) {
                p.sendMessage("Command usage: /medals [place][usage][date]");
                return true;
            }
        }catch(IndexOutOfBoundsException e) {
            p.sendMessage("Command usage: /medals [place][usage][date]");
            return true;
        }

        String placement = null;
        ItemStack item = null;
        ChatColor color = null;
        if(arg3[0].equalsIgnoreCase("first")) {
            placement = "First";
            item = new ItemStack(Material.EMERALD, 1);
            color = ChatColor.LIGHT_PURPLE;
        } else if(arg3[0].equalsIgnoreCase("second")) {
            placement = "Second";
            item = new ItemStack(Material.DIAMOND ,1);
            color = ChatColor.RED;
        } else if(arg3[0].equalsIgnoreCase("third")) {
            placement = "Third";
            item = new ItemStack(Material.GOLD_INGOT, 1);
            color = ChatColor.GOLD;
        } else {
            p.sendMessage("Invalid placement. Valid options are: first, second, third.");
            return true;
        }
        
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(color + placement + " Place Medal");
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(color + "Official PCN " + arg3[1] + " Medal");
        lore.add(color + arg3[2]);

        meta.setLore(lore);

        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 1);
        
        p.getInventory().addItem(item);
        return true;
    }
 }