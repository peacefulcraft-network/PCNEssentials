package net.peacefulcraft.rtp.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class RTPTC implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		List<String> opts = new ArrayList<String>();

		if (arg3.length == 1) {
			for(Object rangeName : Configuration.getRtpRanges().keySet().toArray()) {
				opts.add((String) rangeName);
			}
		} else if (arg3.length == 2) {
			if (Configuration.getAllowedRTPWorlds().size() == 0) {
				for(World world : PCNEssentials.getPluginInstance().getServer().getWorlds()) {
					if (world.getEnvironment() == Environment.NETHER || world.getEnvironment() == Environment.THE_END) {
						continue;
					}

					opts.add(world.getName());
				}
			} else {
				opts.addAll(Configuration.getAllowedRTPWorlds());
			}
		}

		Iterator<String> optsIt = opts.iterator();
		while (optsIt.hasNext()) {
			String suggestion = optsIt.next();
			if (!suggestion.startsWith(arg3[arg3.length - 1])) {
				optsIt.remove();
			}
		}

		return opts;
	}
	
}
