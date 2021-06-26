package net.peacefulcraft.rtp.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class PhantomsKilledListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPhantomDeath(EntityDamageByEntityEvent ev) {
		if(!Configuration.getCompetitionEnabled()) { return; }
    if(!Configuration.getCompetitionName().equalsIgnoreCase("Phantoms Killed")) { return; }

		 // Checking damager is player
		 Entity damager = ev.getDamager();
		 if(!(damager instanceof Player)) { return; }
		 Player p = (Player) damager;

		 // Checking damaged is phantom
		 Entity damaged = ev.getEntity();
		 if(!(damaged instanceof Phantom)) { return; }
		 Phantom phantom = (Phantom) damaged;

		 // Only increment if phantom died
		 if(!(phantom.getHealth() <= ev.getFinalDamage())) { return; }

		 PCNEssentials.getChallengeScoreboard().incrimentScore(p);
	}
}
