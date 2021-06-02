package net.peacefulcraft.rtp.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class Hug implements CommandExecutor {

	private Map<UUID, Map<UUID, Long>> cooldowns;

	public Hug() {
		this.cooldowns = Collections.synchronizedMap(new HashMap<UUID, Map<UUID, Long>>());
		PCNEssentials.getPluginInstance().logNotice(PCNEssentials.messagePrefix + "Hugs: " + ((Configuration.IsHugEnabled())? "enabled" : "disabled"));
	}

	public void tryFreePlayerCooldown(UUID uuid) {
		Map<UUID, Long> interactions = this.cooldowns.get(uuid);
		if (interactions == null) { return; }

		/**
		 * Try to remove all the entries from the Map.
		 * This check allows us to still free up some memory
		 * if we can't evict the whole map
		 */
		Long now = System.currentTimeMillis();
		interactions.forEach((u, timestamp) -> {
			if (now > timestamp) {
				interactions.remove(u);
			}
		});

		// Evict the map if there are no entries left
		if (interactions.size() == 0) {
			this.cooldowns.remove(uuid);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!Configuration.IsHugEnabled()) { sender.sendMessage(PCNEssentials.messagePrefix + "Command disabled."); return true; }

		if (args.length == 0) {
			sender.sendMessage(PCNEssentials.messagePrefix + "Include the username of the player whom you wish to send your affections too.");
			return true;
		}

		Player target = PCNEssentials.getPluginInstance().getServer().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(PCNEssentials.messagePrefix + args[0] + " does not appear to be on the server.");
			return true;
		}


		String displayName = sender.getName();
		// Console and command blocks, etc have no cooldown
		if (sender instanceof Player) {
			PCNEssentials.getPluginInstance().logNotice("is player");
			Player lover = (Player) sender;
			displayName = lover.getDisplayName();
			
			// Check for cooldown
			if (this.cooldowns.containsKey(lover.getUniqueId())) {
				PCNEssentials.getPluginInstance().logNotice("with existing cooldown");
				Map<UUID, Long> sentLove = this.cooldowns.get(lover.getUniqueId());
				Long cooldown = sentLove.get(target.getUniqueId());
			PCNEssentials.getPluginInstance().logNotice("of " + cooldown);


				// Check if cooldown has lapsed to that player
				if (cooldown != null && cooldown > System.currentTimeMillis()) {
					sender.sendMessage(PCNEssentials.messagePrefix + "Sorry, but Cupid can only deliver your affections to a player every " + Configuration.getHugCooldown() + " seconds");
					return true;
				}

				// Store cooldown time
				sentLove.put(target.getUniqueId(), System.currentTimeMillis() + Configuration.getHugCooldown() * 1000);

			// No cooldowns exist yet so create the map and store the cooldown time
			} else {
				PCNEssentials.getPluginInstance().logNotice("with no cooldowns setup");
				Map<UUID, Long> cooldownMap = Collections.synchronizedMap(new HashMap<UUID, Long>());
				cooldownMap.put(target.getUniqueId(), System.currentTimeMillis() + Configuration.getHugCooldown() * 1000);
				this.cooldowns.put(lover.getUniqueId(), cooldownMap);
			}
		}

		// Cooldown block will return if unable to perform action
		target.spawnParticle(Particle.HEART, target.getLocation(), Configuration.getHugHeartEffectCount(), 1.0, 1.0, 1.0);
		target.sendMessage(ChatColor.RED + "[" + ChatColor.DARK_RED + "❤" + ChatColor.RED + "] " + ChatColor.GOLD + displayName + ChatColor.RESET + ChatColor.GRAY + " has sent you their affections.");

		return true;
	}
	
}
