package net.peacefulcraft.rtp.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Monster;

import net.peacefulcraft.rtp.PCNEssentials;

public class Death implements Runnable {
		
	private static List<Entity> entityList = new ArrayList<Entity>();
		public static void setTargets(List<Entity> entityList2) { entityList = entityList2; }
	
	private static World world;
		public static void setWorld(World w) { world = w; }
	
	private static HashMap<EntityType, Integer> kills = new HashMap<EntityType, Integer>();
		public static HashMap<EntityType, Integer> getKills(){ return kills; }
	
	public void run() {
		
		if(entityList.size() > 0) {
			
			Entity target = entityList.get(0);
			if(target != null) {

				//Load the chunk
				Chunk chunk = world.getChunkAt(target.getLocation());
				Entity[] localEntities = chunk.getEntities();
				
				//Kill all unimportant things in that chunk while we have it
				for(Entity e : localEntities) {
					
					if(e instanceof Monster || e instanceof Bat || e instanceof Fish) {
						Damageable ed = (Damageable) e;
						ed.damage(20000);
						entityList.remove(e);
						
						if(kills.keySet().contains(e.getType())) {
							kills.put(e.getType(), 1 + kills.get(e.getType()));
						}else {
							kills.put(e.getType(), 1);
						}
						
					}
					
					//explicitly release the chunk
					chunk = null;
					
				}
			
			}
			
		}else {
			
			//cache entities
			List<Entity> loadedEntities = world.getEntities();			
			for(Entity e : loadedEntities) {
				
				if(e.getTicksLived() > 1200) {
					entityList.add(e);
				}
				
			}
			PCNEssentials.getPluginInstance().getServer().getLogger().log(Level.INFO, entityList.size() + " targets");
			
		}

	}

}
