package net.peacefulcraft.rtp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class ShowChallengeScoreboard implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(PCNEssentials.messagePrefix + "This command is intended for players only.");
      return true;
    } 

    if (!Configuration.getCompetitionEnabled()) {
      sender.sendMessage(PCNEssentials.messagePrefix + "Sorry, there are no active compeitions setup with scoreboard displays.");
      return true;
    }

    if (args.length < 1) {
      sender.sendMessage(PCNEssentials.messagePrefix + "/pcnscore on | /pcnscore off");
      return true;
    }

    if (args[0].equalsIgnoreCase("on")) {
      ((Player) sender).setScoreboard(PCNEssentials.getChallengeScoreboard().getScoreboard());

      Long curTime = System.currentTimeMillis();
      if (curTime < Configuration.getCompetitionStartMS()) {
        // +1 is to avoid saying '0 hours'
        sender.sendMessage(PCNEssentials.messagePrefix + "The " + Configuration.getCompetitionName() + " cometition doesn't start for " + (1+((Configuration.getCompetitionStartMS() - curTime) / 3600000) + " hours."));
      } else if (curTime > Configuration.getCompetitionEndMS()) {
        sender.sendMessage(PCNEssentials.messagePrefix + "The " + Configuration.getCompetitionName() + " cometition ended " + ((curTime - Configuration.getCompetitionEndMS()) / 3600000) + " hours ago. The final scores were...");
      } else {
        sender.sendMessage(PCNEssentials.messagePrefix + "Scoreboard display enabled. Competition started " + ((curTime - Configuration.getCompetitionStartMS()) / 3600000) + " hours ago and ends in " + ((Configuration.getCompetitionEndMS() - curTime) / 1000) + " hours.");
      }
    } else {
      ((Player) sender).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      sender.sendMessage(PCNEssentials.messagePrefix + "Scoreboard display disabled.");
    }

    return true;
  }
  
}
