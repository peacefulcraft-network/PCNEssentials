package net.peacefulcraft.rtp.listeners;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class TurkeyListener implements Listener {
    
    @EventHandler
    public void chickenSpawn(CreatureSpawnEvent e) {
        if(!Configuration.getCompetitionEnabled()) { return; }
        if(!Configuration.getCompetitionName().equalsIgnoreCase("Turkeys Killed")) { return; }

        // Checking natural spawn. We don't want eggs or breeding allowed
        if(!e.getSpawnReason().equals(SpawnReason.NATURAL)) { return; }

        // Checking chicken
        LivingEntity chick = e.getEntity();
        if(!(chick instanceof Chicken)) { return; }

        // Flagging chickens meta data and setting name
        chick.setMetadata("Event", new FixedMetadataValue(PCNEssentials.getPluginInstance(), "Turkey"));
        chick.setCustomName("Thanksgiving Turkey");
        chick.setCustomNameVisible(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void chickenDeath(EntityDamageByEntityEvent e) {
        if(!Configuration.getCompetitionEnabled()) { return; }
        if(!Configuration.getCompetitionName().equalsIgnoreCase("Turkeys Killed")) { return; }

        // Checking damager is player
        Entity damager = e.getDamager();
        if(!(damager instanceof Player)) { return; }
        Player p = (Player) damager;

        // Checking damaged is chicken
        Entity damaged = e.getEntity();
        if(!(damaged instanceof Chicken)) { return; }
        Chicken chick = (Chicken) damaged;

        // Only increment if chicken died
        if(!(chick.getHealth() <= e.getFinalDamage())) { return; }

        // Checking name and meta data
        if(chick.getCustomName() == null || chick.getCustomName().isEmpty()) { return; }
        if(!chick.getCustomName().equalsIgnoreCase("Thanksgiving Turkey")) { return; }
        if(!chick.hasMetadata("Event")) { return; }

        PCNEssentials.getChallengeScoreboard().incrimentScore(p);
    }
}
