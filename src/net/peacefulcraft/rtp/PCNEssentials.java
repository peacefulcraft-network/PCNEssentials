package net.peacefulcraft.rtp;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.rtp.commands.Boots;
import net.peacefulcraft.rtp.commands.Crusade;
import net.peacefulcraft.rtp.commands.Medals;
import net.peacefulcraft.rtp.commands.NightVision;
import net.peacefulcraft.rtp.commands.RTP;
import net.peacefulcraft.rtp.commands.Reload;
import net.peacefulcraft.rtp.configuration.Configuration;
public class PCNEssentials extends JavaPlugin{

	private static PCNEssentials p;
		public static PCNEssentials getPluginInstance() { return p; }
		
	private static Configuration c;
		public static Configuration getPluginConfig() { return c; }

	public PCNEssentials(){
		p = this;
	}
		
	public void onEnable() {
		this.saveDefaultConfig();

		c = new Configuration(this.getConfig());

		this.getCommand("rtp").setExecutor(new RTP(this.getConfig()));
		if(Configuration.getRtpEnabled()) { logNotice("RTP: Enabled"); }
		
		this.getCommand("nv").setExecutor(new NightVision());
		if(this.getConfig().getBoolean("nv.enabled")) { logNotice("NV: Enabled"); }

		this.getCommand("pcn-reload").setExecutor(new Reload());
		this.getCommand("medals").setExecutor(new Medals());
		//this.getCommand("pickaxe").setExecutor(new Pickaxe());
		this.getCommand("pickaxe").setExecutor(new Boots());
		this.getCommand("crusade").setExecutor(new Crusade());
	}
	
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
	}
	
	public void logNotice(String message) {
		this.getLogger().log(Level.INFO, ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET + message);
	}
}
