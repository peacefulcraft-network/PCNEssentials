package net.peacefulcraft.rtp.scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.PCNEssentials;

public class ChallengeScoreboard {
  protected String challengeName;
  protected YamlConfiguration scores;
  protected Scoreboard scoreboard;
    public Scoreboard getScoreboard() { return scoreboard; }
  protected Objective trackedObjective;

  public ChallengeScoreboard(String challengeName) throws IOException, InvalidConfigurationException {
    this.challengeName = challengeName;
    this.scores = new YamlConfiguration();
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    this.trackedObjective = this.scoreboard.registerNewObjective(challengeName.toLowerCase().replaceAll(" ", ""), "foo", ChatColor.GREEN + challengeName);
    this.trackedObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    this.loadData();
  }

  protected void loadData() throws IOException, InvalidConfigurationException {
    File dataFile = new File(PCNEssentials.getPluginInstance().getDataFolder().getPath() + "/challenges/" + challengeName.toLowerCase() + ".yml");
    if (!dataFile.exists()) { return; }
    
    scores.load(dataFile);
    Set<String> keySet = scores.getKeys(false);
    keySet.forEach(key -> {
      String username = scores.getString(key + ".name");
      int score = scores.getInt((key + ".score"));
      this.trackedObjective.getScore(username).setScore(score);
    });
  }

  public void saveData() throws IOException {
    this.scores.save(
      new File(PCNEssentials.getPluginInstance().getDataFolder().getPath() + "/challenges/" + challengeName.toLowerCase() + ".yml")
    );
  }

  public void incrimentScore(Player p) {
    int score = 0;
    String oldName = p.getName();
    if (this.scores.contains(p.getUniqueId().toString())) {
      score = this.scores.getInt(p.getUniqueId().toString() + ".score");
      oldName = this.scores.getString(p.getUniqueId().toString() + ".name");
    }

    scores.set(p.getUniqueId().toString() + ".name", p.getName());
    scores.set(p.getUniqueId().toString() + ".score", ++score);

    Score scoreEntry = this.trackedObjective.getScore(p.getName());
    scoreEntry.setScore(score);

    // Check if the player's name changed. Remove old score entry if it did
    if (!p.getName().equals(oldName)) {
      this.trackedObjective.getScore(oldName).setScore(0);
    }
  }

  /**
   * Method to be called to increment competition totals
   * @param v value of increment
   */
  public void incrimentTotalBy(Integer v) {
    int score = 0;
    score = this.scores.getInt("Total.score");

    scores.set("Total" + ".score", score + v);
    Score scoreEntry = this.trackedObjective.getScore("Total");
    scoreEntry.setScore(score + v);
  }

  public void incrimentScoreBy(Player p, Integer v) {
    int score = 0;
    String oldName = p.getName();
    if (this.scores.contains(p.getUniqueId().toString())) {
      score = this.scores.getInt(p.getUniqueId().toString() + ".score");
      oldName = this.scores.getString(p.getUniqueId().toString() + ".name");
    }

    scores.set(p.getUniqueId().toString() + ".name", p.getName());
    scores.set(p.getUniqueId().toString() + ".score", score + v);

    Score scoreEntry = this.trackedObjective.getScore(p.getName());
    scoreEntry.setScore(score + v);

    // Check if the player's name changed. Remove old score entry if it did
    if (!p.getName().equals(oldName)) {
      this.trackedObjective.getScore(oldName).setScore(0);
    }
  }

  /**
   * Decriment score. If score < floor, score = floor.
   * @param p
   * @param floor
   */
  public void decrimentScore(Player p, Integer floor) {
    int score = 0;
    String oldName = p.getName();
    if (this.scores.contains(p.getUniqueId().toString())) {
      score = this.scores.getInt(p.getUniqueId().toString() + ".score");
      oldName = this.scores.getString(p.getUniqueId().toString() + ".name");
    }

    if (--score < floor) {
      score = floor;
    }

    scores.set(p.getUniqueId().toString() + ".name", p.getName());
    scores.set(p.getUniqueId().toString() + ".score", score);

    Score scoreEntry = this.trackedObjective.getScore(p.getName());
    scoreEntry.setScore(score);

    // Check if the player's name changed. Remove old score entry if it did
    if (!p.getName().equals(oldName)) {
      this.trackedObjective.getScore(oldName).setScore(0);
    }
  }
}
