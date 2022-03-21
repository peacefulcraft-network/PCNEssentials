package net.peacefulcraft.rtp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class CompetitionPickupListener implements Listener {
    
    private List<Object> trackedItems;
    private Location depositLoc;

    public CompetitionPickupListener() {
        trackedItems = new ArrayList<Object>();
        trackedItems.add(Material.valueOf(Configuration.getCompetitionItem()));

        depositLoc = Configuration.getCompetitionDepositLocation();
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent ev) {

        // Only want collection events IN spawn
        if (!Configuration.getCompetitionEnabled()) { return; }
        if (!Configuration.getCompetitionName().contains("Collection Event")) { return; }
        if (!ev.getPlayer().getLocation().getWorld().getName().equals("spawn")) { return; }

        Item item = ev.getItemDrop();

        // Only want tracked items
        if (!trackedItems.contains(item.getItemStack().getType())) { return; }

        Bukkit.getScheduler().runTaskLater(PCNEssentials.getPluginInstance(), () -> {                
            Location relativeDown = item.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
            
            // Comparing dropped item location after allocating time to drop
            if (relativeDown.getBlockX() == depositLoc.getBlockX() && relativeDown.getBlockY() == depositLoc.getBlockY() && relativeDown.getBlockZ() == depositLoc.getBlockZ()) {
                PCNEssentials.getChallengeScoreboard().incrimentScoreBy(ev.getPlayer(), item.getItemStack().getAmount());
            }
        }, 80L);
    }

}
