package net.peacefulcraft.rtp.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.peacefulcraft.rtp.configuration.Configuration;

public class DragonDropsListener implements Listener {

    @EventHandler
    public void DragonDropsEvent(EntityDeathEvent ev) {
        if (!Configuration.getDragonDropsEnabled()) { return; }

        if (ev.getEntity() instanceof EnderDragon) {
            LivingEntity dragon = (EnderDragon)ev.getEntity();

            Location loc = dragon.getLocation();

            loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.ELYTRA, 1));
        }
    }
    
}
