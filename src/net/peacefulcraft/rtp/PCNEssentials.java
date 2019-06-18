package net.peacefulcraft.rtp;
import org.bukkit.plugin.java.JavaPlugin;

import net.peacefulcraft.rtp.commands.NightVision;
import net.peacefulcraft.rtp.commands.RTP;
import net.peacefulcraft.rtp.commands.Terminator;
import net.peacefulcraft.rtp.tasks.Death;;
public class PCNEssentials extends JavaPlugin{

	private static PCNEssentials p;
		public static PCNEssentials getPluginInstance() { return p; }
		
	public PCNEssentials(){
		p = this;
	}
		
	public void onEnable() {
		this.getCommand("rtp").setExecutor(new RTP());
		this.getCommand("nv").setExecutor(new NightVision());
		this.getCommand("terminator").setExecutor(new Terminator());
		
		//Death.setWorld(this.getServer().getWorld("world"));
		//this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Death(), 250, 250);
	}
	
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
	}
	
}
