package net.peacefulcraft.rtp.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.peacefulcraft.rtp.configuration.Configuration;

public class ShulkerDropsListener implements Listener {
    
    @EventHandler
    public void ShulkerDropsEvent(EntityDeathEvent ev) {
        if (!Configuration.getShulkerDropsEnabled()) { return; }

        if (!ev.getEntityType().equals(EntityType.SHULKER)) { return; }

        ItemStack shells = new ItemStack(Material.SHULKER_SHELL, 2);
        ev.getDrops().clear();

        Location loc = ev.getEntity().getLocation();

        loc.getWorld().dropItemNaturally(loc, shells);
    }
}
