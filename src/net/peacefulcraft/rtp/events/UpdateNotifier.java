package net.peacefulcraft.rtp.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.peacefulcraft.rtp.PCNEssentials;

public class UpdateNotifier implements Listener{
  
  private static UpdateNotifier _this = null;
  public static UpdateNotifier getInstance() { return UpdateNotifier._this; }

  private static String updateUrl;
  public static String setUpdateUrl(String updateUrl) { return UpdateNotifier.updateUrl; }

  public UpdateNotifier(String updateUrl) {
    UpdateNotifier.updateUrl = updateUrl;
    _this = this;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent ev) {
    Player p = ev.getPlayer();
    
    if (p.isOp() || p.hasPermission("pcn.staff")) {
      p.sendMessage(PCNEssentials.messagePrefix + "An update for PCNEssentials is available. Download it from " + updateUrl);
    }
  }
}