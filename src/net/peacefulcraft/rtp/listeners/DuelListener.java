package net.peacefulcraft.rtp;

import com.meteordevelopments.duels.api.event.match.MatchEndEvent;
import com.meteordevelopments.duels.api.user.User;
import com.meteordevelopments.duels.api.user.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class DuelListener implements Listener {
    private final PCNEssentials plugin;

    public DuelListener(PCNEssentials plugin) {
        this.plugin = plugin;
    }

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		if (!plugin.getConfig().getBoolean("duelUpdates.enabled", true)) return;

		java.util.UUID winnerUUID = event.getWinner();
		java.util.UUID loserUUID = event.getLoser();

		if (winnerUUID == null || loserUUID == null) return;
		
		com.meteordevelopments.duels.api.Duels duels = org.bukkit.Bukkit.getServicesManager().load(com.meteordevelopments.duels.api.Duels.class);
    
		if (duels == null) return; // Safety check

		// Change DuelsAPI to Duels
		com.meteordevelopments.duels.api.user.UserManager um = duels.getUserManager();
		com.meteordevelopments.duels.api.user.User wUser = um.get(winnerUUID);
		com.meteordevelopments.duels.api.user.User lUser = um.get(loserUUID);

		String winnerName = org.bukkit.Bukkit.getOfflinePlayer(winnerUUID).getName();
		String loserName = org.bukkit.Bukkit.getOfflinePlayer(loserUUID).getName();

		if (winnerName == null) winnerName = winnerUUID.toString();
		if (loserName == null) loserName = loserUUID.toString();

		// The rest of your JSON string building...
		String description = "Player **" + winnerName + "** defeated **" + loserName + "**!\\n\\n"
				+ "**Winner: " + winnerName + "**\\n"
				+ "▫ Rating: " + wUser.getRating() + "\\n"
				+ "▫ W/L: " + wUser.getWins() + "/" + wUser.getLosses() + "\\n\\n"
				+ "**Loser: " + loserName + "**\\n"
				+ "▫ Rating: " + lUser.getRating() + "\\n"
				+ "▫ W/L: " + lUser.getWins() + "/" + lUser.getLosses();

		String msg = "{"
				+ "\"embeds\": [{"
				+ "\"title\": \"⚔️  A Duel Has Concluded!\","
				+ "\"description\": \"" + description + "\","
				+ "\"color\": 5585548"
				+ "}]"
				+ "}";

		plugin.sendToDiscord(msg);
	}	
}
