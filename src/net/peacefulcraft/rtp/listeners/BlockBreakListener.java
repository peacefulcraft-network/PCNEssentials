package net.peacefulcraft.rtp.listeners;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import net.peacefulcraft.rtp.PCNEssentials;

/**
 * Responsible for random drop events
 */
public class BlockBreakListener implements Listener {
    
    //Consistent random for class
    private static final Random RANDOM = new Random();

    //Consistent list of all possible materials to be dropped in mc
    private static final List<Material> MATERIALS = Collections.unmodifiableList(Arrays.asList(Material.values()));

    //Size of material list
    private static final int SIZE = MATERIALS.size();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        //If random drops aren't enabled we quit
        if(!PCNEssentials.isRandomDropsEnabled()) { return; }
        
        //Getting block in event and clearing
        Block block = e.getBlock();
        if(block.getType().equals(Material.CHEST)) {
            for(ItemStack item : block.getDrops()) {
                if(item.getType().equals(Material.CHEST)) {
                    if(item.getAmount() == 0) { 
                        block.getDrops().remove(item); 
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            } 
        } else if(block.getType().equals(Material.TRAPPED_CHEST)) {
            for(ItemStack item : block.getDrops()) {
                if(item.getType().equals(Material.TRAPPED_CHEST)) {
                    if(item.getAmount() == 0) { 
                        block.getDrops().remove(item); 
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }   
        } else {
            block.getDrops().clear();
        }

        ItemStack item = new ItemStack(MATERIALS.get(RANDOM.nextInt(SIZE)), 1);

        block.getDrops().add(item);
    }
}
