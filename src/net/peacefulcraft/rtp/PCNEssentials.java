package net.peacefulcraft.rtp;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.commands.Medals;
import net.peacefulcraft.rtp.commands.NightVision;
import net.peacefulcraft.rtp.commands.RTP;
import net.peacefulcraft.rtp.commands.Reload;
public class PCNEssentials extends JavaPlugin{

	private static PCNEssentials p;
		public static PCNEssentials getPluginInstance() { return p; }
		
	public PCNEssentials(){
		p = this;
	}
		
	public void onEnable() {
		this.saveDefaultConfig();

		if(this.getConfig().getBoolean("rtp.enabled")) {
			this.getCommand("rtp").setExecutor(new RTP(this.getConfig()));
			logNotice("RTP: Enabled");
		}
		
		if(this.getConfig().getBoolean("nv.enabled")) {
			this.getCommand("nv").setExecutor(new NightVision());
			logNotice("NV: Enabled");
		}

		this.getCommand("pcn-reload").setExecutor(new Reload());
		this.getCommand("medals").setExecutor(new Medals());
	}
	
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
	}
	
	public void logNotice(String message) {
		this.getLogger().log(Level.INFO, ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET + message);
	}
}
