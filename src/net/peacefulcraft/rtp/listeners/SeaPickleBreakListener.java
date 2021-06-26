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
            for (String[] result : lookupResults) {
              ParseResult parsedResult = coAPI.parseResult(result);
              if (trackedBlocks.contains(parsedResult.getType()) && parsedResult.getActionId() == 1) {
                PCNEssentials.getPluginInstance().logNotice("Detected challenge block break from unatural generation - omitting");
                return;
              }
            }

            PCNEssentials.getPluginInstance().logNotice("Detected challenge block break from natural generation, but with unrelated modifications.");
            drops.forEach((item) -> {
              PCNEssentials.getChallengeScoreboard().incrimentScoreBy(ev.getPlayer(), item.getAmount());
            });
          }
        }, 100L);
      }
    }
	}
}
