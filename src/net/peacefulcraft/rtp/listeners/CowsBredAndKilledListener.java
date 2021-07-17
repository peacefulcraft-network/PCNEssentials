package net.peacefulcraft.rtp.listeners;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class CowsBredAndKilledListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCowBreed(EntityBreedEvent ev) {
		Long curTime = System.currentTimeMillis();
		if (curTime < Configuration.getCompetitionStartMS() || curTime > Configuration.getCompetitionEndMS()) { return; }

		if (ev.getMother().getType() == EntityType.COW || ev.getMother().getType() == EntityType.MUSHROOM_COW) {
			if (ev.getBreeder() instanceof Player) {
				PCNEssentials.getChallengeScoreboard().incrimentScore((Player) ev.getBreeder());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCowKilled(EntityDamageByEntityEvent ev) {
		Long curTime = System.currentTimeMillis();
		if (curTime < Configuration.getCompetitionStartMS() || curTime > Configuration.getCompetitionEndMS()) { return; }

		if (ev.getDamager() instanceof Player && (ev.getEntityType() == EntityType.COW || ev.getEntityType() == EntityType.MUSHROOM_COW)) {
			Damageable cow = (Damageable) ev.getEntity();
			if (cow.getHealth() <= ev.getFinalDamage()) {
				PCNEssentials.getChallengeScoreboard().decrimentScore((Player) ev.getDamager(), 0);
			}
		}
	}
}
