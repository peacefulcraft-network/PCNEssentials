package net.peacefulcraft.rtp.scoreboard;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;

import net.peacefulcraft.rtp.PCNEssentials;

public class ContestScoreboard extends ChallengeScoreboard {

    public ContestScoreboard(String contestName) throws IOException, InvalidConfigurationException {
        super(contestName);
    }

    @Override
    public void saveData() throws IOException {
        this.scores.save(
            new File(PCNEssentials.getPluginInstance().getDataFolder().getPath() + "/contests/" + challengeName.toLowerCase() + ".yml")
        );
    }
    
}
