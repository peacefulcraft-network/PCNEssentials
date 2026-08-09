package net.peacefulcraft.rtp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class Reload implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("pcn.staff")) {
            PCNEssentials plugin = PCNEssentials.getPluginInstance();

            // Re-read config.yml from disk, then re-point the Configuration layer at the fresh
            // FileConfiguration object and rebuild derived state (RTP ranges, defaults, integrity).
            plugin.reloadConfig();
            Configuration.reload(plugin.getConfig());

            // Simple cached toggle
            PCNEssentials.randomDropsEnabled = Configuration.getRandomEnabled();

            // Competition: only act on a state change to avoid double-scheduling save timers
            boolean competitionShouldRun = Configuration.getCompetitionEnabled();
            boolean competitionRunning = PCNEssentials.getChallengeScoreboard() != null;
            if (competitionShouldRun && !competitionRunning) {
                plugin.enableCompetition();
            } else if (!competitionShouldRun && competitionRunning) {
                plugin.disableCompetition();
            }

            // Welcome-reward listener: update in place (no re-registration -> no double rewards)
            if (plugin.getWbRewards() != null) {
                plugin.getWbRewards().updateConfig(
                    plugin.getConfig().getDouble("payout-amount", 50.0),
                    plugin.getConfig().getStringList("keywords")
                );
            }

            // Config-driven timers: cancel + reschedule with current enable/frequency values
            plugin.startScheduledTasks();

            if (Configuration.getRtpEnabled()) {
                plugin.logNotice("RTP: Enabled");
            }
            if (Configuration.nvEnabled()) {
                plugin.getCommand("nv").setExecutor(new NightVision());
                plugin.logNotice("NV: Enabled");
            }

            sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]"
                + ChatColor.BLUE + "PCNEssentials config reloaded (RTP ranges, toggles, rewards, scheduled tasks).");
        }

        return true;
	}
}
