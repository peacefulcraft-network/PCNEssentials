package net.peacefulcraft.rtp.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.peacefulcraft.rtp.PCNEssentials;

public class TimeVoteCommand implements CommandExecutor, TabCompleter, Listener {

    private static final long DAY_TIME = 1000L;
    private static final long NIGHT_TIME = 13000L;

    private final PCNEssentials plugin;
    private final Map<UUID, ActiveVote> activeVotes = new HashMap<>();

    public TimeVoteCommand(PCNEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "player only");
            return true;
        }

        return handleCommand((Player) sender, label, args);
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String[] commandParts = event.getMessage().substring(1).trim().split("\\s+");
        if (commandParts.length == 0 || !commandParts[0].equalsIgnoreCase("voting")) {
            return;
        }

        event.setCancelled(true);
        String[] args = Arrays.copyOfRange(commandParts, 1, commandParts.length);
        handleCommand(event.getPlayer(), commandParts[0], args);
    }

    private boolean handleCommand(Player player, String label, String[] args) {
        if (args.length != 1 || (!isTime(args[0]))) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <day|night>");
            return true;
        }

        if (activeVotes.containsKey(player.getWorld().getUID())) {
            castVote(player, args[0]);
        } else {
            startVote(player, args[0]);
        }
        return true;
    }

    private void startVote(Player initiator, String requestedTime) {
        World world = initiator.getWorld();
        String worldName = world.getName();
        if (plugin.getConfig().getStringList("time-voting.excluded-worlds").stream()
                .anyMatch(name -> name.equalsIgnoreCase(worldName))) {
            initiator.sendMessage(ChatColor.RED + "Time voting is disabled in this world.");
            return;
        }

        if (activeVotes.containsKey(world.getUID())) {
            initiator.sendMessage(ChatColor.RED + "A time vote is already active in this world.");
            return;
        }

        int duration = Math.max(1, plugin.getConfig().getInt("time-voting.voting-time", 60));
        Set<UUID> eligiblePlayers = new HashSet<>();
        for (Player player : world.getPlayers()) {
            eligiblePlayers.add(player.getUniqueId());
        }

        ActiveVote vote = new ActiveVote(world, requestedTime.toLowerCase(Locale.ROOT), duration, eligiblePlayers);
        vote.votes.put(initiator.getUniqueId(), vote.requestedTime);
        activeVotes.put(world.getUID(), vote);
        updateBossBar(vote, duration);
        for (Player player : world.getPlayers()) {
            vote.bossBar.addPlayer(player);
            player.sendMessage(ChatColor.GOLD + "A vote has started to make it " + ChatColor.YELLOW
                    + vote.requestedTime + ChatColor.GOLD + " in " + worldName + "!");
            player.sendMessage(ChatColor.GRAY + "Vote with " + ChatColor.WHITE + "/voting day"
                    + ChatColor.GRAY + " or " + ChatColor.WHITE + "/voting night" + ChatColor.GRAY + ".");
        }

        vote.task = new BukkitRunnable() {
            private int secondsLeft = duration;

            @Override
            public void run() {
                secondsLeft--;
                if (secondsLeft <= 0) {
                    finishVote(vote);
                    cancel();
                    return;
                }
                updateBossBar(vote, secondsLeft);
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void castVote(Player player, String selectedTime) {
        ActiveVote vote = activeVotes.get(player.getWorld().getUID());
        if (vote == null) {
            player.sendMessage(ChatColor.RED + "There is no active time vote in this world.");
            return;
        }
        if (!vote.eligiblePlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You were not in this world when the vote started.");
            return;
        }

        vote.votes.put(player.getUniqueId(), selectedTime.toLowerCase(Locale.ROOT));
        player.sendMessage(ChatColor.GREEN + "Your vote for " + selectedTime.toLowerCase(Locale.ROOT)
                + " has been recorded.");
    }

    private void finishVote(ActiveVote vote) {
        activeVotes.remove(vote.world.getUID());
        vote.bossBar.removeAll();

        long requestedVotes = vote.votes.values().stream()
                .filter(vote.requestedTime::equals)
                .count();
        String opposingTime = vote.requestedTime.equals("day") ? "night" : "day";
        long opposingVotes = vote.votes.values().stream()
                .filter(opposingTime::equals)
                .count();

        String result;
        if (requestedVotes > opposingVotes) {
            vote.world.setTime(vote.requestedTime.equals("day") ? DAY_TIME : NIGHT_TIME);
            result = ChatColor.GREEN + "The vote passed! It is now " + vote.requestedTime + ".";
        } else {
            result = ChatColor.RED + "The vote did not pass" + (requestedVotes == opposingVotes ? " (tie)." : ".");
        }

        for (Player player : vote.world.getPlayers()) {
            player.sendMessage(result + ChatColor.GRAY + " (" + requestedVotes + " for, "
                    + opposingVotes + " against)");
        }
    }

    private void updateBossBar(ActiveVote vote, int secondsLeft) {
        vote.bossBar.setTitle(ChatColor.YELLOW + "Vote for " + vote.requestedTime + ChatColor.WHITE
                + " - " + secondsLeft + "s left");
        vote.bossBar.setProgress(Math.max(0.0, Math.min(1.0, secondsLeft / (double) vote.duration)));
    }

    public void shutdown() {
        for (ActiveVote vote : activeVotes.values()) {
            if (vote.task != null) {
                vote.task.cancel();
            }
            vote.bossBar.removeAll();
        }
        activeVotes.clear();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }
        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> options = new ArrayList<>();
        for (String option : Arrays.asList("day", "night")) {
            if (option.startsWith(prefix)) {
                options.add(option);
            }
        }
        return options;
    }

    private boolean isTime(String value) {
        return value.equalsIgnoreCase("day") || value.equalsIgnoreCase("night");
    }

    private static final class ActiveVote {
        private final World world;
        private final String requestedTime;
        private final int duration;
        private final Set<UUID> eligiblePlayers;
        private final Map<UUID, String> votes = new HashMap<>();
        private final BossBar bossBar;
        private BukkitTask task;

        private ActiveVote(World world, String requestedTime, int duration, Set<UUID> eligiblePlayers) {
            this.world = world;
            this.requestedTime = requestedTime;
            this.duration = duration;
            this.eligiblePlayers = eligiblePlayers;
            this.bossBar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
        }
    }
}
