package net.peacefulcraft.rtp.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;
import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class SeaPickleBreakListener implements Listener {

  private CoreProtectAPI coAPI;
  private List<Object> trackedBlocks;

	public SeaPickleBreakListener() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
    if (plugin == null || !(plugin instanceof CoreProtect)) {
      PCNEssentials.getPluginInstance().logNotice("CoreProtect not found. Block break exploit mitigations are not enabled.");
      return;
    }

    coAPI = ((CoreProtect) plugin).getAPI();
    if (!((CoreProtect) plugin).isEnabled()) {
      PCNEssentials.getPluginInstance().logNotice("CoreProtect found, but not properly enabled. Block break exploit mitigations are not enabled.");
      return;
    }

    trackedBlocks = new ArrayList<Object>();
    trackedBlocks.add(Material.SEA_PICKLE);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreakBlock(BlockBreakEvent ev) {
		if(!Configuration.getCompetitionEnabled()) { return; }
    if(!Configuration.getCompetitionName().equalsIgnoreCase("Sea Pickles")) { return; }

		Long curTime = System.currentTimeMillis();
		if (curTime < Configuration.getCompetitionStartMS() || curTime > Configuration.getCompetitionEndMS()) { return; }

    /**
     * There is not a way to see what items are dropped on a break so we have to re-roll the break,
     * get a list of items to drop, drop them, and then use the list.size() as the score should the
     * break pass the CO checks.
     */
    Collection<ItemStack> drops = ev.getBlock().getDrops(ev.getPlayer().getInventory().getItemInMainHand(), ev.getPlayer());
    ev.setDropItems(false);
    drops.forEach((item) -> {
      ev.getBlock().getLocation().getWorld().dropItemNaturally(ev.getBlock().getLocation(), item);
    });

    if (trackedBlocks.contains(ev.getBlock().getType())) {
      if (coAPI == null) {
        PCNEssentials.getChallengeScoreboard().incrimentScore(ev.getPlayer());
      } else {
        Bukkit.getScheduler().runTaskLater(PCNEssentials.getPluginInstance(), () -> {
          List<String[]> lookupResults = coAPI.blockLookup(ev.getBlock(), Integer.MAX_VALUE);
          if (lookupResults == null || lookupResults.size() == 0) {
            PCNEssentials.getPluginInstance().logNotice("Detected challenge block break from natural generation.");
            drops.forEach((item) -> {
              PCNEssentials.getChallengeScoreboard().incrimentScoreBy(ev.getPlayer(), item.getAmount());
            });
          } else {
            /**
             * Only check if the most recent action was related to the challenge.
             * Since this challenge could involve farming, we must account for placements
             * from a while ago that are no longer relavent.
             */
            boolean secondMostRecent = false;
            for (String[] result : lookupResults) {
              ParseResult parsedResult = coAPI.parseResult(result);
              System.out.println(parsedResult.getTime() + " - " + parsedResult.getActionString() + " - " + parsedResult.getType());

               /**
               * Look for the second recent block mod at this location which involved THE (singular) challenge block.
               * If it was a placement, then we ignore. If it was a removal than we're good. The most recent interaction
               * will be the break that we're trying to count so we need to ignore it. The second most recent should either be
               * a place if this is an attempted cheat, or a removal from previous farm cycles.
               * 
               * CO results come with most recent to oldest. First action involving challenge block is what we want.
               * (0=removed, 1=placed, 2=interaction)
               */
              if (trackedBlocks.contains(parsedResult.getType())) {

                // Skip the place or break in the result set
                if (parsedResult.getActionId() == 1 || parsedResult.getActionId() == 0) {
                  if (!secondMostRecent) {
                    System.out.println("Found first related entry. Skipping");
                    secondMostRecent = true;
                    continue;
                  }
                }

                if (parsedResult.getActionId() == 1) {
                  PCNEssentials.getPluginInstance().logNotice("Detected challenge block break from unatural generation - omitting");
                  return;

                } else if (parsedResult.getActionId() == 0) {
                  PCNEssentials.getPluginInstance().logNotice("Detected challenge block break from natural generation, but with unrelated modifications.");
                  drops.forEach((item) -> {
                    PCNEssentials.getChallengeScoreboard().incrimentScoreBy(ev.getPlayer(), item.getAmount());
                  });
                  return;
                }
              }
            }
          }
        }, 100L);
      }
    }
	}
}
