package net.peacefulcraft.rtp.configuration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.peacefulcraft.rtp.PCNEssentials;

public class Configuration {
  private static FileConfiguration c;

  public static boolean nvEnabled() { return c.getBoolean("nv.enabled"); }
  public static void setNvEnabled(boolean isEneabled) { c.set("nv.enabled", isEneabled); }
  
  public static boolean getRtpEnabled() { return c.getBoolean("rtp.enabled"); }
  public static void setRtpEnalbed(boolean isEneabled) { c.set("rtp.enabled", isEneabled); }

  public static int getRtpResistanceDuration() { return c.getInt("rtp.resistance_duration"); }
  public static void setRtpResistanceDuration(int duration) { c.set("rtp.resistance_duration", duration); }

  public static boolean getCompetitionEnabled() { return c.getBoolean("competition.enabled"); }
  public static String getCompetitionName() { return c.getString("competition.name"); }
  public static Long getCompetitionStartMS() { return c.getLong("competition.start_ms"); }
  public static Long getCompetitionEndMS() { return c.getLong("competition.end_ms"); }

  public static String getCompetitionItem() { return c.getString("competition.item_name"); }
  public static Location getCompetitionDepositLocation() {
    int x = c.getInt("competition.deposit_location.x");
    int y = c.getInt("competition.deposit_location.y");
    int z = c.getInt("competition.deposit_location.z");
    String world = c.getString("competition.deposit_location.world");

    return new Location(Bukkit.getServer().getWorld(world), x, y, z);
  }

  public static boolean getRandomEnabled() { return c.getBoolean("random.enabled"); }

  public static boolean getCrusadeEnabled() { return c.getBoolean("crusade.enabled"); }

  public static boolean IsHugEnabled() { return c.getBoolean("hug.enabled"); }
  public static int getHugHeartEffectCount() { return c.getInt("hug.heart_count"); }
  public static int getHugCooldown() { return c.getInt("hug.cooldown"); }

  public static boolean getShulkerDropsEnabled() { return c.getBoolean("shulker_drops.enabled"); }

  public static boolean getDragonDropsEnabled() { return c.getBoolean("ender_dragon_drops.enabled"); }

  public Configuration(FileConfiguration c) {
    Configuration.c = c;

    URL defaultConfigurationURI = getClass().getClassLoader().getResource("config.yml");
    File defaultConfigurationFile = new File(defaultConfigurationURI.toString());
    YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(defaultConfigurationFile);
    c.setDefaults(defaultConfiguration);

    loadComplexValues();
    ensureIntegrity();
  }

  private static void loadComplexValues() {
    for(String range : c.getConfigurationSection("rtp.ranges").getKeys(false)) {
      ConfigurationSection cfgs = c.getConfigurationSection("rtp.ranges." + range);
      if(!cfgs.contains("min")) { continue; }
      if(!cfgs.contains("max")) { continue; }

      int min = cfgs.getInt("min");
      int max = cfgs.getInt("max");

      rtpRanges.put(range, new RTPRadiusLimit(min, max));
      PCNEssentials.getPluginInstance().logNotice("Registered RTP Range " + range + " [" + min + ", " + max + "]");
    }
    PCNEssentials.getPluginInstance().logNotice(rtpRanges.size() + " RTP ranges loaded");

  }

  private static HashMap<String, RTPRadiusLimit> rtpRanges = new HashMap<String,RTPRadiusLimit>();
  public static HashMap<String, RTPRadiusLimit> getRtpRanges() { return rtpRanges; }
  public static void setRtpRanges(HashMap<String, RTPRadiusLimit> ranges) {
    rtpRanges = ranges;
    c.set("rtp.ranges", rtpRanges);
  }
  public static void setRtpRange(String key, RTPRadiusLimit range) {
    rtpRanges.put(key, range);
    c.set("rtp.ranges." + key + ".min", range.getMinRadius());
    c.set("rtp.ranges." + key + ".max", range.getMaxRadius());
  }
  public static List<String> getAllowedRTPWorlds() {
    // Return allowed world list, or default to all allowed if no list provided
    if (c.contains("rtp.allowed_worlds")) {
      return Collections.unmodifiableList((List<String>) c.getList("rtp.allowed_worlds"));
    } else {
      List<String> worldNames = new ArrayList<String>();
      PCNEssentials.getPluginInstance().getServer().getWorlds().forEach((world) -> {
        worldNames.add(world.getName());
      });
      return Collections.unmodifiableList(worldNames);
    }
  }

  /**
   * Set configuration values to their defaults if they do not exist in the file already
   */
  private static void ensureIntegrity() {
    if (!c.contains("hug.enabled")) {
      c.set("hug.enabled", true);
    }

    if (!c.contains("hug.heart_count")) {
      c.set("hug.heart_count", 15);
    }

    if (!c.contains("hug.cooldown")) {
      c.set("hug.cooldown", 15);
    }

    if (!c.contains("rtp.allowed_worlds")) {
      c.set("rtp.allowed_worlds", new ArrayList<String>());
    }
  }
}