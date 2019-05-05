package net.peacefulcraft.rtp;
import org.bukkit.plugin.java.JavaPlugin;

import net.peacefulcraft.rtp.commands.NightVision;
import net.peacefulcraft.rtp.commands.RTP;

public class PCNEssentials extends JavaPlugin{

	public void onEnable() {
		this.getCommand("rtp").setExecutor(new RTP());
		this.getCommand("nv").setExecutor(new NightVision());
	}
	
	public void onDisable() {
		
	}
	
}
