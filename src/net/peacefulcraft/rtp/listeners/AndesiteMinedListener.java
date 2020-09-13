package net.peacefulcraft.rtp.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.peacefulcraft.rtp.PCNEssentials;

public class AndesiteMinedListener implements Listener {
  
  @EventHandler
  public void onBreakBlock(BlockBreakEvent ev) {
    if (ev.getBlock().getType() == Material.ANDESITE) {
      PCNEssentials.getChallengeScoreboard().incrimentScore(ev.getPlayer());
    }
  }
}
