package net.peacefulcraft.rtp.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Monster;

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
			if(target.getLocation() != null) {
				
				Location loc = target.getLocation();
			
				if(world.isChunkLoaded(loc.getBlockX(), loc.getBlockZ())){
					
					entityList.remove(target);			
				
				}else {
				
					Chunk chunk = world.getChunkAt(loc);
					Entity[] localEntities = chunk.getEntities();
					
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
					}
					
				}
			
			}
			
		}else {
			
			List<Entity> loadedEntities = world.getEntities();			
			for(Entity e : loadedEntities) {
				
				Location loc = e.getLocation();
				if(!world.isChunkLoaded(loc.getBlockX(), loc.getBlockZ())) {
					entityList.add(e);
				}
				
			}
			
		}

	}

}
