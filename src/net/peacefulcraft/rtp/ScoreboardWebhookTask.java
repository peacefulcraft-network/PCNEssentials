package net.peacefulcraft.rtp;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardWebhookTask extends BukkitRunnable {

    // Assuming your main class is named 'MyPlugin'. Replace with your actual main class name.
    private final PCNEssentials plugin; 
    private final String objectiveName;

    public ScoreboardWebhookTask(PCNEssentials plugin, String objectiveName) {
        this.plugin = plugin;
        this.objectiveName = objectiveName;
    }

    @Override
	public void run() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Objective objective = scoreboard.getObjective(objectiveName);

		if (objective == null) {
			plugin.getLogger().warning("Scoreboard objective '" + objectiveName + "' not found!");
			return;
		}

		StringBuilder scoreBuilder = new StringBuilder("**📊 Periodic Scoreboard Update:**\n");
		boolean hasEntries = false;
		
		for (String entry : scoreboard.getEntries()) {
			Score score = objective.getScore(entry);
			if (score.isScoreSet()) {
				scoreBuilder.append(entry).append(": ").append(score.getScore()).append("\n");
				hasEntries = true;
			}
		}

		// If the scoreboard has no active scores, don't send an empty message to Discord
		if (!hasEntries) {
			plugin.getLogger().warning("Scoreboard objective '" + objectiveName + "' exists but has no active scores to send.");
			return;
		}

		// Create a safe JSON payload using Google's Gson (built into Spigot)
		com.google.gson.JsonObject json = new com.google.gson.JsonObject();
		json.addProperty("content", scoreBuilder.toString());
		
		String jsonPayload = json.toString();

		// Send it off asynchronously
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			plugin.sendToDiscord(jsonPayload);
		});
	}
}
