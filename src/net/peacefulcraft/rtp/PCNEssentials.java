package net.peacefulcraft.rtp;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import net.milkbowl.vault.economy.Economy;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.collectionevent.CollectionEvent;
import net.peacefulcraft.rtp.commands.BarrierCommand;
import net.peacefulcraft.rtp.commands.Boots;
import net.peacefulcraft.rtp.commands.CompetitionCommands;
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
import net.peacefulcraft.rtp.commands.TimeVoteCommand;
import net.peacefulcraft.rtp.configuration.Configuration;
import net.peacefulcraft.rtp.listeners.BlockBreakListener;
import net.peacefulcraft.rtp.listeners.CowsBredAndKilledListener;
import net.peacefulcraft.rtp.listeners.DragonDropsListener;
import net.peacefulcraft.rtp.listeners.GraniteMinedListener;
import net.peacefulcraft.rtp.listeners.PhantomsKilledListener;
import net.peacefulcraft.rtp.listeners.SeaPickleBreakListener;
import net.peacefulcraft.rtp.listeners.ShulkerDropsListener;
import net.peacefulcraft.rtp.listeners.TurkeyListener;
import net.peacefulcraft.rtp.scoreboard.ChallengeScoreboard;
public class PCNEssentials extends JavaPlugin{

	public static final String release = "0.0.12";

	private static PCNEssentials p;
		public static PCNEssentials getPluginInstance() { return p; }
		
	private static Configuration c;
		public static Configuration getPluginConfig() { return c; }
	
	private static Economy econ=null;

	public static final String messagePrefix = ChatColor.BLUE + "[" + ChatColor.GREEN  + "PCN" + ChatColor.BLUE + "] " + ChatColor.RESET + ChatColor.GRAY;

	public static boolean randomDropsEnabled;
		public static boolean isRandomDropsEnabled() { return randomDropsEnabled; }
		public static void setRandomDrops(boolean b) { randomDropsEnabled = b; }

	public static ChallengeScoreboard challengeScoreboard;
		public static ChallengeScoreboard getChallengeScoreboard() { return challengeScoreboard; }
		
	public static CollectionEvent collectionEvent;
		public static CollectionEvent getCollectionEvent() { return collectionEvent; }

	private wbListener wbRewards;
		public wbListener getWbRewards() { return wbRewards; }

	// Handles for config-driven timers so they can be cancelled/rescheduled on reload
	private BukkitTask plotUpdateTask;
	private BukkitTask scoreboardTask;
	private TimeVoteCommand timeVoteCommand;

	public void onEnable() {
		p = this;
		this.saveDefaultConfig();

		c = new Configuration(this.getConfig());

		randomDropsEnabled = Configuration.getRandomEnabled();
		if (randomDropsEnabled) { logNotice("RandomDrops: Enabled"); }
		if (!randomDropsEnabled) { logNotice("RandomDrops: Disabled"); }

		if (Configuration.getCompetitionEnabled()) {
			enableCompetition();
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
		this.getCommand("pcncompetition").setExecutor(new CompetitionCommands());
		this.getCommand("barrier").setExecutor(new BarrierCommand());

		timeVoteCommand = new TimeVoteCommand(this);
		this.getCommand("voting").setExecutor(timeVoteCommand);
		this.getCommand("voting").setTabCompleter(timeVoteCommand);
		getServer().getPluginManager().registerEvents(timeVoteCommand, this);

		//Registering listeners
		getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
		getServer().getPluginManager().registerEvents(new ShulkerDropsListener(), this);
		getServer().getPluginManager().registerEvents(new DragonDropsListener(), this);
		getServer().getPluginManager().registerEvents(new GlobalNick(), this);
		
        
        //let console know
        getLogger().info("GlobalNickModule has been synchronized with LuckPerms!");
		
		
		// Duels listener, only if duels installed
		if (getServer().getPluginManager().isPluginEnabled("Duels")) {
			getServer().getPluginManager().registerEvents(new DuelListener(this), this);
		} else {
			getLogger().warning("Duels not found! Duel notifications will not be sent.");
		}

		// 2 & 3. config-driven timers (plot updates + event scoreboard webhook)
		startScheduledTasks();

		// 4. set up economy and register wb rewards listener
		if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        wbRewards = new wbListener(this, getConfig().getDouble("payout-amount", 50.0), getConfig().getStringList("keywords"));
        getServer().getPluginManager().registerEvents(wbRewards, this);

		UpdateCheck updateCheck = new UpdateCheck();
		// On a healthy server, this checks every hour
		updateCheck.runTaskTimerAsynchronously(this, 0, 72000);
	}
	
	public void onDisable() {
		if (timeVoteCommand != null) {
			timeVoteCommand.shutdown();
		}
		disableCompetition();

		this.getServer().getScheduler().cancelTasks(this);
	}

	/**
	 * Cancels any running config-driven timers, then (re)schedules the enabled ones from config.
	 * Safe to call repeatedly (startup and /pcn-reload) — it will not double-schedule.
	 */
	public void startScheduledTasks() {
		if (plotUpdateTask != null) { plotUpdateTask.cancel(); plotUpdateTask = null; }
		if (scoreboardTask != null) { scoreboardTask.cancel(); scoreboardTask = null; }

		FileConfiguration cfg = getConfig();

		// Plot build competition updates, only if PlotSquared is installed
		if (getServer().getPluginManager().isPluginEnabled("PlotSquared")) {
			if (cfg.getBoolean("plotBuildCompUpdates.enabled", false)) {
				long ticks = cfg.getInt("plotBuildCompUpdates.frequency", 60) * 1200L;
				plotUpdateTask = new PlotUpdateTask(this).runTaskTimerAsynchronously(this, 20L, ticks);
			}
		} else {
			getLogger().warning("PlotSquared not found! Plot updates will not be sent.");
		}

		// Passing minecraft scoreboard standings to discord for events
		if (cfg.getBoolean("eventScoreboardUpdates.enabled", false)) {
			String objectiveName = cfg.getString("eventScoreboardUpdates.objectiveName", "scoreboard");
			String messageTitle = cfg.getString("eventScoreboardUpdates.messageTitle", "🏆 Event Scoreboard Standings");
			long ticks = cfg.getInt("eventScoreboardUpdates.frequency", 30) * 1200L;
			scoreboardTask = new ScoreboardWebhookTask(this, objectiveName, messageTitle).runTaskTimer(this, 100L, ticks);
		}
	}

	/**
	 * Configures and enables scoreboard.
	 */
	public boolean enableCompetition() {
		Configuration.setCompetitionEnabled(true);

		String competitionName = Configuration.getCompetitionName();
		if (competitionName.isEmpty()) {
			logNotice("Empty competition name loaded from config. Competition loading cancelled");
			return false;
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
				return false;
			}
		}
		return true;
	}

	/**
	 * Saves and disabled competition and scoreboard
	 */
	public boolean disableCompetition() {
		if (challengeScoreboard == null) { return true; }
		try {
			challengeScoreboard.saveData();
			
			Configuration.setCompetitionEnabled(false);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			logError("Unable to load or save data file for competition");
			return false;
		}
	}
	
	//discord webhook sender
	public void sendToDiscord(String discordPost) {
		// Pull the string dynamically from the config.yml
        // If the config option doesn't exist, it falls back to an empty string safely
        final String webhookUrl = this.getConfig().getString("discord-webhook-url", ""); 

        if (webhookUrl.isEmpty() || webhookUrl.equals("YOUR_WEBHOOK_URL_HERE")) {
            this.getLogger().warning("Discord webhook URL is not configured in config.yml!");
            return;
        }
        // Implement your existing webhook sending logic here
        // Remember to use the color decimal: 5585548

        try {
            java.net.URL url = new java.net.URL(webhookUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Java-Webhook");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            try (java.io.OutputStream os = connection.getOutputStream()) {
                os.write(discordPost.getBytes());
                os.flush();
            }

            connection.getInputStream().close(); // Finalize the request
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
		} else if (boardName.contains("Collection Event")) {
			//getServer().getPluginManager().registerEvents(new CompetitionPickupListener(), this);
			collectionEvent = new CollectionEvent();
			logNotice("Competition: Registered collection event listener");
		}
	}
	
	public void logError(String message) {
		this.getLogger().log(Level.SEVERE, ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET + message);
	}

	public void logNotice(String message) {
		this.getLogger().log(Level.INFO, ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET + message);
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }
}
