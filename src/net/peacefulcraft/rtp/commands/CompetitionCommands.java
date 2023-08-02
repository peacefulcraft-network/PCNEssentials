package net.peacefulcraft.rtp.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class CompetitionCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pcn.staff")) { return true; }

        // Setting the competition item
        if (args[0].equalsIgnoreCase("setitem")) {
            if (args.length < 2) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setitem [item name]");
                return true;
            } 

            try {
                Material mat = Material.valueOf(args[1]);
                Configuration.setCompetitionItem(mat.name());
                //sender.sendMessage(PCNEssentials.messagePrefix + "Successfully set collection item to: " + Configuration.getCompetitionItem());
                return true;
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setitem [item name] given illegal item name");
                return true;
            } catch (NullPointerException exx) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setitem [item name] | something went wrong. Contact admin.");
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("setname")) {
            if (args.length < 3) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setname [name] [Collection Event true/false]");
                return true;
            }

            try {
                if (Boolean.valueOf(args[2])) {
                    Configuration.setCompetitionName(args[1] + " Collection Event");
                } else {
                    Configuration.setCompetitionName(args[1]);
                }
                sender.sendMessage(PCNEssentials.messagePrefix + "Successfully set competition name to: " + Configuration.getCompetitionName());
                return true;
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setname [name] [Collection Event true/false]");
                return true;
            } catch (NullPointerException ex) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setname [name] [Collection Event true/false]\n Something went wrong contact admin.");
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (args.length < 2) {
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition toggle [true/false]");
                return true;
            }

            try {
                if (Boolean.valueOf(args[1])) {
                    if (PCNEssentials.getPluginInstance().enableCompetition()) {
                        sender.sendMessage(PCNEssentials.messagePrefix + "Successfully enabled competitions.");
                    } else {
                        sender.sendMessage(PCNEssentials.messagePrefix + "Internal error. Contact admin");
                    }
                } else {
                    if (PCNEssentials.getPluginInstance().disableCompetition()) {
                        sender.sendMessage(PCNEssentials.messagePrefix + "Successfully disabled competitions.");
                    } else {
                        sender.sendMessage(PCNEssentials.messagePrefix + "Internal error contact admin");
                    }
                }
                return true;
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition toggle [true/false]");
                return true;
            }
        }

        /*
        if (args[0].equalsIgnoreCase("setstarttime")) {
            if (args.length < 2) {
                sender.sendMessage(
                    PCNEssentials.messagePrefix + "/pcncompetition setstarttime [days of comp]\n This command sets start time to NOW."
                );
                return true;
            }

            try {
                Integer days = Integer.valueOf(args[1]);
                Configuration.setCompetitionStartMS(System.currentTimeMillis());
                Configuration.setCompetitionEndMS(System.currentTimeMillis() + (days * 86400000));
                sender.sendMessage(PCNEssentials.messagePrefix + "Successfully set competition for: " + days + " days");
                return true;
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                sender.sendMessage(PCNEssentials.messagePrefix + "/pcncompetition setstarttime [days of comp]\n This command sets start time to NOW.");
                return true;
            }
        }*/

        return true;
    }
    
}
