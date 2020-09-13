package net.peacefulcraft.rtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.peacefulcraft.rtp.PCNEssentials;

public class ShowChallengeScoreboard implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(PCNEssentials.messagePrefix + "This command is intended for players only.");
      return true;
    } 

    if (args.length < 1) {
      sender.sendMessage(PCNEssentials.messagePrefix + "/pcnscore on | /pcnscore off");
    }

    if (args[0].equalsIgnoreCase("on")) {
      ((Player) sender).setScoreboard(PCNEssentials.getChallengeScoreboard().getScoreboard());
      sender.sendMessage(PCNEssentials.messagePrefix + "Scoreboard display enabled.");
    } else {
      ((Player) sender).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      sender.sendMessage(PCNEssentials.messagePrefix + "Scoreboard display disabled.");
    }

    return true;
  }
  
}
