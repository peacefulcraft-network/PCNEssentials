package net.peacefulcraft.rtp.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Boots implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) { return true; }     
        if(!sender.hasPermission("pcn.staff")) { return true; }
        
        Player p = (Player) sender;
        try {
            if(args[0].isEmpty()){
                p.sendMessage("Command usage: /boots [place]");
                return true;
            }
        } catch(IndexOutOfBoundsException ex) {
            p.sendMessage("Command usage: /boots [place]");
            return true;
        }

        String placement = null;
        ChatColor color = null;
        ItemStack pick = new ItemStack(Material.DIAMOND_BOOTS,1);
        ItemMeta meta = pick.getItemMeta();
        if(args[0].equalsIgnoreCase("first")) {
            placement = "First";
            color = ChatColor.LIGHT_PURPLE;
            meta.addEnchant(Enchantment.DEPTH_STRIDER, 6, true);
            meta.addEnchant(Enchantment.PROTECTION_FALL, 7, true);
        } else if(args[0].equalsIgnoreCase("second")) {
            placement = "Second";
            color = ChatColor.RED;
            meta.addEnchant(Enchantment.DEPTH_STRIDER, 5, true);
            meta.addEnchant(Enchantment.PROTECTION_FALL, 6, true);
        } else if(args[0].equalsIgnoreCase("third")) {
            placement = "Third";
            color = ChatColor.GOLD;
            meta.addEnchant(Enchantment.DIG_SPEED, 4, true);
            meta.addEnchant(Enchantment.PROTECTION_FALL, 5, true);
        } else {
            p.sendMessage("Invalid placement: First, Second, Third");
            return true;
        }
        
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.DURABILITY, 3, false);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

        meta.setDisplayName(color + "PCN Devil's Boots");

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(color + "" + placement + " Place Sprinter!");
        lore.add(color + "Honorary Sprinting Competition Boots");

        pick.setItemMeta(meta);

        p.getInventory().addItem(pick);

        return true;
    }
}