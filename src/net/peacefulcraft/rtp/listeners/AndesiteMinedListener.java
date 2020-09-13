package net.peacefulcraft.rtp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;
import net.peacefulcraft.rtp.PCNEssentials;

public class AndesiteMinedListener implements Listener {
  
  private CoreProtectAPI coAPI;
  private List<Object> trackedBlocks;

  public AndesiteMinedListener() {
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
    trackedBlocks.add(Material.ANDESITE);
  }

  @EventHandler
  public void onBreakBlock(BlockBreakEvent ev) {
    if (trackedBlocks.contains(ev.getBlock().getType())) {
      if (coAPI == null) {
        PCNEssentials.getChallengeScoreboard().incrimentScore(ev.getPlayer());
      } else {
        List<String[]> lookupResults = coAPI.blockLookup(ev.getBlock(), Integer.MAX_VALUE);
        if (lookupResults == null || lookupResults.size() == 0) {
          PCNEssentials.getChallengeScoreboard().incrimentScore(ev.getPlayer());
        } else {
          for (String[] result : lookupResults) {
            ParseResult parsedResult = coAPI.parseResult(result);
            if (trackedBlocks.contains(parsedResult.getType())) {
              ev.getPlayer().sendMessage("Omitted");
              return;
            }
          }

          PCNEssentials.getChallengeScoreboard().incrimentScore(ev.getPlayer());
        }
      }
    }
  }
}
