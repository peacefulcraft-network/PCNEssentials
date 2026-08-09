package net.peacefulcraft.rtp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.ess3.api.events.AfkStatusChangeEvent;

import java.util.*;

public class wbListener implements Listener {

    private final PCNEssentials plugin;
    private double payoutAmount;
    private List<String> keywords;
    
    private final Map<UUID, Long> recentJoins = new HashMap<>();
    private final Set<UUID> rewardedPlayers = new HashSet<>();
    private final long WELCOME_WINDOW_MS = 30000; // 30 seconds to say welcome

    // Updated constructor to receive config arguments directly
    public wbListener(PCNEssentials plugin, double payoutAmount, List<String> keywords) {
        this.plugin = plugin;
        this.payoutAmount = payoutAmount;
        this.keywords = keywords;
    }

    /** Re-apply config values in place so /pcn-reload takes effect without re-registering the listener. */
    public void updateConfig(double payoutAmount, List<String> keywords) {
        this.payoutAmount = payoutAmount;
        this.keywords = keywords;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        recentJoins.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        rewardedPlayers.clear(); 
    }
    
    @EventHandler
	public void onPlayerAfkChange(AfkStatusChangeEvent event) {
		// We only care when a player is COMING BACK from AFK (value goes from true to false)
		if (!event.getValue()) { 
			UUID uuid = event.getAffected().getBase().getUniqueId();
			recentJoins.put(uuid, System.currentTimeMillis());
			rewardedPlayers.clear();
		}
	}

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player chatter = event.getPlayer();
        String message = event.getMessage().toLowerCase();
        long now = System.currentTimeMillis();

        // 1. Check if anyone has joined within the valid time window
        boolean standardWelcomeWindowActive = recentJoins.values().stream()
                .anyMatch(joinTime -> (now - joinTime) < WELCOME_WINDOW_MS);

        if (!standardWelcomeWindowActive) return;

        // 2. Prevent players from welcoming themselves
        boolean isNewPlayerChatting = recentJoins.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(chatter.getUniqueId()) && (now - entry.getValue()) < WELCOME_WINDOW_MS);
        
        if (isNewPlayerChatting) return;

        // 3. Ensure player hasn't already been rewarded for this specific join
        if (rewardedPlayers.contains(chatter.getUniqueId())) return;

        // 4. Match against constructor-passed keywords
        boolean containsKeyword = keywords.stream().anyMatch(message::contains);

        if (containsKeyword) {
            rewardedPlayers.add(chatter.getUniqueId());

            // Jump back to primary server thread for thread-safe Vault deposits
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getEconomy().depositPlayer(chatter, payoutAmount);
                chatter.sendMessage("§a+$" + payoutAmount + " for welcoming a player!");
            });
        }
    }
}
