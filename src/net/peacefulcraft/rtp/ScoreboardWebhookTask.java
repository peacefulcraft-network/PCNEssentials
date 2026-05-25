package net.peacefulcraft.rtp;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;

public class ScoreboardWebhookTask extends BukkitRunnable {

    private final PCNEssentials plugin; // Assuming your main class name
    private final String objectiveName;
    private final String messageTitle;

    public ScoreboardWebhookTask(PCNEssentials plugin, String objectiveName, String messageTitle) {
        this.plugin = plugin;
        this.objectiveName = objectiveName;
        this.messageTitle = messageTitle;
    }

    @Override
    public void run() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective == null) {
            plugin.getLogger().warning("Scoreboard objective '" + objectiveName + "' not found!");
            return;
        }

        // 1. Collect and store the player-score pairs
        List<Map.Entry<String, Integer>> scoresList = new ArrayList<>();
        
        for (String entry : scoreboard.getEntries()) {
            Score score = objective.getScore(entry);
            if (score.isScoreSet()) {
                scoresList.add(new AbstractMap.SimpleEntry<>(entry, score.getScore()));
            }
        }

        // If the scoreboard is empty, don't ping Discord
        if (scoresList.isEmpty()) {
            return;
        }

        // 2. Sort the entries: Highest score to Lowest score
        scoresList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // 3. Format the sorted leaderboard text
        StringBuilder leaderboardBuilder = new StringBuilder();
        int placement = 1;
        for (Map.Entry<String, Integer> player : scoresList) {
            leaderboardBuilder.append("`#")
                              .append(placement++)
                              .append("` **")
                              .append(player.getKey())
                              .append("**: ")
                              .append(player.getValue())
                              .append("\n");
        }

        // 4. Construct the Discord Embed Payload via GSON
        JsonObject rootJson = new JsonObject();
        JsonArray embedsArray = new JsonArray();
        JsonObject embedObject = new JsonObject();

        // The bold header title inside the embed box
        embedObject.addProperty("title", messageTitle);
        // The actual leaderboard rankings inside the box description
        embedObject.addProperty("description", leaderboardBuilder.toString());
        // Set the border color of the box using your decimal color code (5585548 = a nice greenish teal)
        embedObject.addProperty("color", 5585548); 

        // Nest the structures: Root -> Embeds Array -> Embed Object
        embedsArray.add(embedObject);
        rootJson.add("embeds", embedsArray);

        String jsonPayload = rootJson.toString();

        // 5. Fire off to your main class method asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.sendToDiscord(jsonPayload);
        });
    }
}
