package net.peacefulcraft.rtp.configuration;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

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

  public static boolean getRandomEnabled() { return c.getBoolean("random.enabled"); }

  public static boolean getCrusadeEnabled() { return c.getBoolean("crusade.enabled"); }
  public Configuration(FileConfiguration c) {
    Configuration.c = c;

    URL defaultConfigurationURI = getClass().getClassLoader().getResource("config.yml");
    File defaultConfigurationFile = new File(defaultConfigurationURI.toString());
    YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(defaultConfigurationFile);
    c.setDefaults(defaultConfiguration);

    loadComplexValues();
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
}