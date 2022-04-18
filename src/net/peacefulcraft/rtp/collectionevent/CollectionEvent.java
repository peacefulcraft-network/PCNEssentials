package net.peacefulcraft.rtp.collectionevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import net.peacefulcraft.rtp.PCNEssentials;
import net.peacefulcraft.rtp.configuration.Configuration;

public class CollectionEvent {
    
    private Location collectionLoc;
    private List<Map<?, ?>> collectionMats;
    private HashMap<String, Integer> collectionPointDict;

    public CollectionEvent() {
        this.collectionLoc = Configuration.getCompetitionDepositLocation();
        this.collectionMats = Configuration.getCompetitionItemList();
        this.collectionPointDict = new HashMap<>();

        PCNEssentials.getPluginInstance().logNotice(collectionMats.toString());

        for (Map<?, ?> map : collectionMats) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> test = (Map<String, Integer>)map;
            collectionPointDict.putAll(test);
        }

        PCNEssentials.getPluginInstance().logNotice(collectionPointDict.toString());

        Bukkit.getScheduler().runTaskTimer(PCNEssentials.getPluginInstance(), () -> {
            for (Entity e : collectionLoc.getWorld().getNearbyEntities(collectionLoc, 2, 2, 2)) {
                if (e instanceof Item) {
                    Item item = (Item)e;
                    if (collectionPointDict.containsKey(item.getItemStack().getType().toString())) {
                        UUID uuid = item.getThrower();

                        int points = collectionPointDict.get(item.getItemStack().getType().toString());

                        PCNEssentials.getChallengeScoreboard().incrimentScoreBy(Bukkit.getServer().getPlayer(uuid), item.getItemStack().getAmount() * points);
                        PCNEssentials.getChallengeScoreboard().incrimentTotalBy(item.getItemStack().getAmount() * points);
                        item.remove();
                    }
                }
            }
        }, 20, 20);
    }

}
