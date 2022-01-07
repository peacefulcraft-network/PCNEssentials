package net.peacefulcraft.rtp;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.commands.Boots;
import net.peacefulcraft.rtp.commands.Crusade;
import net.peacefulcraft.rtp.commands.Hug;
import net.peacefulcraft.rtp.commands.MakeTurkey;
import net.peacefulcraft.rtp.commands.Medals;
import net.peacefulcraft.rtp.commands.NightVision;
import net.peacefulcraft.rtp.commands.PumpkinMedal;
import net.peacefulcraft.rtp.commands.RTP;
import net.peacefulcraft.rtp.commands.RTPTC;
import net.peacefulcraft.rtp.commands.Reload;
import net.peacefulcraft.rtp.commands.ShowChallengeScoreboard;
import net.peacefulcraft.rtp.commands.ToggleDrops;
import net.peacefulcraft.rtp.configuration.Configuration;
import net.peacefulcraft.rtp.listeners.GraniteMinedListener;
import net.peacefulcraft.rtp.listeners.PhantomsKilledListener;
import net.peacefulcraft.rtp.listeners.SeaPickleBreakListener;
import net.peacefulcraft.rtp.listeners.ShulkerDropsListener;
import net.peacefulcraft.rtp.listeners.BlockBreakListener;
import net.peacefulcraft.rtp.listeners.CowsBredAndKilledListener;
import net.peacefulcraft.rtp.listeners.DragonDropsListener;
import net.peacefulcraft.rtp.listeners.TurkeyListener;
import net.peacefulcraft.rtp.scoreboard.ChallengeScoreboard;
public class PCNEssentials extends JavaPlugin{

	public static final String release = "0.0.6";

	private static PCNEssentials p;
		public static PCNEssentials getPluginInstance() { return p; }
		
	private static Configuration c;
		public static Configuration getPluginConfig() { return c; }

	public static final String messagePrefix = ChatColor.BLUE + "[" + ChatColor.GREEN  + "PCN" + ChatColor.BLUE + "] " + ChatColor.RESET + ChatColor.GRAY;

	public static boolean randomDropsEnabled;
		public static boolean isRandomDropsEnabled() { return randomDropsEnabled; }
		public static void setRandomDrops(boolean b) { randomDropsEnabled = b; }

	public static ChallengeScoreboard challengeScoreboard;
		public static ChallengeScoreboard getChallengeScoreboard() { return challengeScoreboard; }
		
	public void onEnable() {
		p = this;
		this.saveDefaultConfig();

		c = new Configuration(this.getConfig());

		randomDropsEnabled = Configuration.getRandomEnabled();
		if (randomDropsEnabled) { logNotice("RandomDrops: Enabled"); }
		if (!randomDropsEnabled) { logNotice("RandomDrops: Disabled"); }

		if (Configuration.getCompetitionEnabled()) {
			String competitionName = Configuration.getCompetitionName();
			if (competitionName.isEmpty()) {
				logNotice("Empty competition name loaded from config. Competition loading cancelled");
			} else {
				try {
					challengeScoreboard = new ChallengeScoreboard(competitionName);
					registerCompetitionListener(competitionName);
	
					// Save the stuff every 5 minutes
					Bukkit.getScheduler().runTaskTimer(this, () -> {
						try {
							challengeScoreboard.saveData();
						} catch (IOException e) {
							e.printStackTrace();
							logError("An error occured while attempting to save challenge data. Some data could be lost.");
						}
					}, 60000, 60000);
	
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
					logError("Unable to load challenge data file for Andesite Mined competition.");
				}
			}
		}
		this.getCommand("pcnscore").setExecutor(new ShowChallengeScoreboard());

		this.getCommand("rtp").setExecutor(new RTP(this.getConfig()));
		this.getCommand("rtp").setTabCompleter(new RTPTC());
		if(Configuration.getRtpEnabled()) { logNotice("RTP: Enabled"); }
		
		this.getCommand("nv").setExecutor(new NightVision());
		if(this.getConfig().getBoolean("nv.enabled")) { logNotice("NV: Enabled"); }

		this.getCommand("randomDrops").setExecutor(new ToggleDrops());

		this.getCommand("pcn-reload").setExecutor(new Reload());
		this.getCommand("medals").setExecutor(new Medals());
		this.getCommand("pumpkinmedal").setExecutor(new PumpkinMedal());
		//this.getCommand("pickaxe").setExecutor(new Pickaxe());
		this.getCommand("pickaxe").setExecutor(new Boots());
		this.getCommand("crusade").setExecutor(new Crusade());
		this.getCommand("maketurkey").setExecutor(new MakeTurkey());
		this.getCommand("hug").setExecutor(new Hug());

		//Registering listeners
		getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
		getServer().getPluginManager().registerEvents(new ShulkerDropsListener(), this);
		getServer().getPluginManager().registerEvents(new DragonDropsListener(), this);

		UpdateCheck updateCheck = new UpdateCheck();
		// On a healthy server, this checks every hour
		updateCheck.runTaskTimerAsynchronously(this, 0, 72000);
	}
	
	public void onDisable() {
		try {
			if (challengeScoreboard != null) {
				challengeScoreboard.saveData();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logError("An error occured while attempting to save challenge data. Some data has been lost.");
		}

		this.getServer().getScheduler().cancelTasks(this);
	}

	private void registerCompetitionListener(String boardName) {
		if(boardName.equalsIgnoreCase("Granite Mined")) {
			getServer().getPluginManager().registerEvents(new GraniteMinedListener(), this);
			logNotice("Competition: Registered Granite Mined listener.");
		} else if(boardName.equalsIgnoreCase("Turkeys Killed")) {
			getServer().getPluginManager().registerEvents(new TurkeyListener(), this);
			logNotice("Competition: Registered Turkeys Killed listener.");
		} else if(boardName.equalsIgnoreCase("Phantoms Killed")) {
			getServer().getPluginManager().registerEvents(new PhantomsKilledListener(), this);
			logNotice("Competition: Registered Phantoms Killed listener.");
		} else if(boardName.equalsIgnoreCase("Sea Pickles")) {
			getServer().getPluginManager().registerEvents(new SeaPickleBreakListener(), this);
			logNotice("Competition: Registered Sea Pickles Collected listener.");		
		} else if (boardName.equalsIgnoreCase("Cows Bred")) {
			getServer().getPluginManager().registerEvents(new CowsBredAndKilledListener(), this);
			logNotice("Competition: Registered cow breeding and killing listeners.");		
		}
	}
	
	public void logError(String message) {
		this.getLogger().log(Level.SEVERE, ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET + message);
	}

	public void logNotice(String message) {
		this.getLogger().log(Level.INFO, ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET + message);
	}
}
