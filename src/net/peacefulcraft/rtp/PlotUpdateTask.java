package net.peacefulcraft.rtp;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.implementations.DoneFlag;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotUpdateTask extends BukkitRunnable {
    
    private final PCNEssentials plugin;

    public PlotUpdateTask(PCNEssentials plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        // Map to store: OwnerUUID -> "PlayerName (Rating: X/10)"
        // Using a Map automatically prevents duplicate entries for the same owner
        Map<UUID, String> finishedPlots = new HashMap<>();

        PlotAreaManager manager = PlotSquared.get().getPlotAreaManager();

        for (PlotArea area : manager.getAllPlotAreas()) {
            for (Plot plot : area.getPlots()) {
                
                // Check if the plot has the DoneFlag
                if (plot.getFlags().stream().anyMatch(flag -> flag instanceof DoneFlag)) {
                    
                    UUID ownerUUID = plot.getOwner();
                    if (ownerUUID == null) continue;

                    // 1. Get Player Name
                    OfflinePlayer op = Bukkit.getOfflinePlayer(ownerUUID);
                    String displayName = (op.getName() != null) ? op.getName() : ownerUUID.toString();

                    // 2. Get Rating (PlotSquared returns double)
                    // If no one has rated it, it usually returns 0.0
                    double rating = plot.getAverageRating();
                    
                    // Format the string for this player
                    String entry = String.format("%s (Rating: %.1f/10)", displayName, rating);
                    
                    // Put in map. If owner has 2 plots, the last one found will be the one shown.
                    finishedPlots.put(ownerUUID, entry);
                }
            }
        }

        if (!finishedPlots.isEmpty()) {
            // Join all the map values with newlines
            String namesList = String.join("\\n- ", finishedPlots.values());
            
            String discordPost = "{"
                + "\"embeds\": [{"
                + "\"title\": \"✅ Build Event: Current Finished Plots\","
                + "\"description\": \"List of players:\\n- " + namesList + "\","
                + "\"color\": 5585548" 
                + "}]"
                + "}";
            
            plugin.sendToDiscord(discordPost);
        }
    }
}
