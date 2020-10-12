package net.peacefulcraft.rtp.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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

    /**List of all wood like materials */
    private static List<Material> WOOD_LIST = new ArrayList<Material>();

    /**
     * List of all earthen materials 
     * Also includes standard building items
     */
    private static List<Material> EARTH_LIST = new ArrayList<Material>();

    /**List of all nether and end items */
    private static List<Material> NETHER_END_LIST = new ArrayList<Material>();

    private static List<Material> BLACKLIST = new ArrayList<Material>();

    //Size of material list
    private static final int SIZE = MATERIALS.size();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        //If random drops aren't enabled we quit
        if(!PCNEssentials.isRandomDropsEnabled()) { return; }

        if(e.getBlock().getDrops().size() == 0) { return; }
        
        //Set cancel dropping items
        e.setDropItems(false);

        Block block = e.getBlock();


        List<ItemStack> items = new ArrayList<>();
        int fortuneLevel = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int silkLevel = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH);
        
        /**
         * Different logic for fortune, silk touch, neither
         * All have 4/5 chance to do as intended and 1/5 chance to be random
         */
        if(fortuneLevel != 0) {
            if(RANDOM.nextInt(4) == 0) {
                for(int i = 1; i <= fortuneLevel; i++) {
                    items.add(getItemStack(block));
                }
            } else {
                ItemStack temp = getItemStack(block);
                temp.setAmount(fortuneLevel);
                items.add(temp);
            }
        } else if(silkLevel != 0) {
            if(RANDOM.nextInt(4) == 0) {
                items.add(getItemStack(block));
            } else {
                items.add(new ItemStack(block.getType(), 1));
            }
        } else {
            items.add(getItemStack(block));
        }

        //Dropping new item naturally at location
        for(ItemStack item : items) {
            try {
                World world = e.getBlock().getWorld();
                world.dropItemNaturally(block.getLocation(), item);
            } catch (IllegalArgumentException ex) {
                PCNEssentials.getPluginInstance().logNotice(item.getType().toString() + " is not droppable.");
                BLACKLIST.add(item.getType());
            }
        }
    }

    /**
     * Intercepting explosion block damage
     */
    /*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void explosionEvent(EntityExplodeEvent e) {
        for(Block block : e.blockList()) {
            ItemStack item = getItemStack(block);

            try {
                World world = block.getWorld();
                world.dropItemNaturally(block.getLocation(), item);
            } catch (IllegalArgumentException ex) {
                PCNEssentials.getPluginInstance().logError(item.getType().toString() + " is not dropablle.");
                BLACKLIST.add(item.getType());
            }
        }
    }*/

    /**
     * Helper method to fetch random item drop
     * @param block Block that was hit during event
     * @return ItemStack of random item
     */
    private ItemStack getItemStack(Block block) {
        ItemStack item;
        do {
            if(RANDOM.nextInt(4) == 0) {
                item = new ItemStack(MATERIALS.get(RANDOM.nextInt(SIZE)), 1);
            } else {
                if(EARTH_LIST.contains(block.getType())) {
                    item = new ItemStack(EARTH_LIST.get(RANDOM.nextInt(EARTH_LIST.size())), 1);
                } else if(NETHER_END_LIST.contains(block.getType())) {
                    item = new ItemStack(NETHER_END_LIST.get(RANDOM.nextInt(NETHER_END_LIST.size())), 1);
                } else if(WOOD_LIST.contains(block.getType())) {
                    item = new ItemStack(WOOD_LIST.get(RANDOM.nextInt(WOOD_LIST.size())), 1);
                } else {
                    item = new ItemStack(MATERIALS.get(RANDOM.nextInt(SIZE)), 1);
                }
            }
        } while (BLACKLIST.contains(item.getType()));

        return item;
    }

    // Adding all the items to their respective lists
    static {
        BLACKLIST.add(Material.AIR);
        BLACKLIST.add(Material.CAVE_AIR);
        BLACKLIST.add(Material.LEGACY_AIR);
        BLACKLIST.add(Material.VOID_AIR);
        BLACKLIST.add(Material.BARRIER);
        BLACKLIST.add(Material.LEGACY_BARRIER);
        BLACKLIST.add(Material.BEDROCK);
        BLACKLIST.add(Material.LEGACY_BEDROCK);
        BLACKLIST.add(Material.COMMAND_BLOCK);
        BLACKLIST.add(Material.COMMAND_BLOCK_MINECART);
        BLACKLIST.add(Material.LEGACY_COMMAND);
        BLACKLIST.add(Material.LEGACY_COMMAND_MINECART);
        BLACKLIST.add(Material.LEGACY_COMMAND_CHAIN);
        BLACKLIST.add(Material.LEGACY_COMMAND_REPEATING);
        BLACKLIST.add(Material.END_PORTAL);
        BLACKLIST.add(Material.END_GATEWAY);
        BLACKLIST.add(Material.LEGACY_END_CRYSTAL);
        BLACKLIST.add(Material.LEGACY_ENDER_PORTAL);
        BLACKLIST.add(Material.LEGACY_END_GATEWAY);
        BLACKLIST.add(Material.FIRE);
        BLACKLIST.add(Material.NETHER_PORTAL);

        /**
         * WOOD LIST
         */
        WOOD_LIST.add(Material.OAK_SAPLING); //< Oak Items
        WOOD_LIST.add(Material.OAK_SLAB);
        WOOD_LIST.add(Material.OAK_STAIRS);
        WOOD_LIST.add(Material.OAK_PLANKS);
        WOOD_LIST.add(Material.OAK_LOG);
        WOOD_LIST.add(Material.OAK_WOOD);
        WOOD_LIST.add(Material.OAK_FENCE);
        WOOD_LIST.add(Material.OAK_FENCE_GATE);
        WOOD_LIST.add(Material.OAK_DOOR);
        WOOD_LIST.add(Material.OAK_TRAPDOOR);
        WOOD_LIST.add(Material.OAK_LEAVES);
        WOOD_LIST.add(Material.OAK_SIGN);
        WOOD_LIST.add(Material.OAK_BOAT);
        WOOD_LIST.add(Material.OAK_PRESSURE_PLATE);
        WOOD_LIST.add(Material.OAK_BUTTON);
        WOOD_LIST.add(Material.BIRCH_SAPLING); //< Birch Items
        WOOD_LIST.add(Material.BIRCH_SLAB);
        WOOD_LIST.add(Material.BIRCH_STAIRS);
        WOOD_LIST.add(Material.BIRCH_PLANKS);
        WOOD_LIST.add(Material.BIRCH_LOG);
        WOOD_LIST.add(Material.BIRCH_WOOD);
        WOOD_LIST.add(Material.BIRCH_FENCE);
        WOOD_LIST.add(Material.BIRCH_FENCE_GATE);
        WOOD_LIST.add(Material.BIRCH_DOOR);
        WOOD_LIST.add(Material.BIRCH_TRAPDOOR);
        WOOD_LIST.add(Material.BIRCH_LEAVES);
        WOOD_LIST.add(Material.BIRCH_SIGN);
        WOOD_LIST.add(Material.BIRCH_BOAT);
        WOOD_LIST.add(Material.BIRCH_PRESSURE_PLATE);
        WOOD_LIST.add(Material.BIRCH_BUTTON);
        WOOD_LIST.add(Material.SPRUCE_SAPLING); //< Spruce items
        WOOD_LIST.add(Material.SPRUCE_SLAB);
        WOOD_LIST.add(Material.SPRUCE_STAIRS);
        WOOD_LIST.add(Material.SPRUCE_PLANKS);
        WOOD_LIST.add(Material.SPRUCE_LOG);
        WOOD_LIST.add(Material.SPRUCE_WOOD);
        WOOD_LIST.add(Material.SPRUCE_FENCE);
        WOOD_LIST.add(Material.SPRUCE_FENCE_GATE);
        WOOD_LIST.add(Material.SPRUCE_DOOR);
        WOOD_LIST.add(Material.SPRUCE_TRAPDOOR);
        WOOD_LIST.add(Material.SPRUCE_LEAVES);
        WOOD_LIST.add(Material.SPRUCE_SIGN);
        WOOD_LIST.add(Material.SPRUCE_BOAT);
        WOOD_LIST.add(Material.SPRUCE_PRESSURE_PLATE);
        WOOD_LIST.add(Material.SPRUCE_BUTTON);
        WOOD_LIST.add(Material.JUNGLE_SAPLING); //< Jungle items
        WOOD_LIST.add(Material.JUNGLE_SLAB);
        WOOD_LIST.add(Material.JUNGLE_STAIRS);
        WOOD_LIST.add(Material.JUNGLE_PLANKS);
        WOOD_LIST.add(Material.JUNGLE_LOG);
        WOOD_LIST.add(Material.JUNGLE_WOOD);
        WOOD_LIST.add(Material.JUNGLE_FENCE);
        WOOD_LIST.add(Material.JUNGLE_FENCE_GATE);
        WOOD_LIST.add(Material.JUNGLE_DOOR);
        WOOD_LIST.add(Material.JUNGLE_TRAPDOOR);
        WOOD_LIST.add(Material.JUNGLE_LEAVES);
        WOOD_LIST.add(Material.JUNGLE_SIGN);
        WOOD_LIST.add(Material.JUNGLE_BOAT);
        WOOD_LIST.add(Material.JUNGLE_PRESSURE_PLATE);
        WOOD_LIST.add(Material.JUNGLE_BUTTON);
        WOOD_LIST.add(Material.ACACIA_SAPLING); //< Acacia items
        WOOD_LIST.add(Material.ACACIA_SLAB);
        WOOD_LIST.add(Material.ACACIA_STAIRS);
        WOOD_LIST.add(Material.ACACIA_PLANKS);
        WOOD_LIST.add(Material.ACACIA_LOG);
        WOOD_LIST.add(Material.ACACIA_WOOD);
        WOOD_LIST.add(Material.ACACIA_FENCE);
        WOOD_LIST.add(Material.ACACIA_FENCE_GATE);
        WOOD_LIST.add(Material.ACACIA_DOOR);
        WOOD_LIST.add(Material.ACACIA_TRAPDOOR);
        WOOD_LIST.add(Material.ACACIA_LEAVES);
        WOOD_LIST.add(Material.ACACIA_SIGN);
        WOOD_LIST.add(Material.ACACIA_BOAT);
        WOOD_LIST.add(Material.ACACIA_PRESSURE_PLATE);
        WOOD_LIST.add(Material.ACACIA_BUTTON);
        WOOD_LIST.add(Material.DARK_OAK_SAPLING); //< Dark oak items
        WOOD_LIST.add(Material.DARK_OAK_SLAB);
        WOOD_LIST.add(Material.DARK_OAK_STAIRS);
        WOOD_LIST.add(Material.DARK_OAK_PLANKS);
        WOOD_LIST.add(Material.DARK_OAK_LOG);
        WOOD_LIST.add(Material.DARK_OAK_WOOD);
        WOOD_LIST.add(Material.DARK_OAK_FENCE);
        WOOD_LIST.add(Material.DARK_OAK_FENCE_GATE);
        WOOD_LIST.add(Material.DARK_OAK_DOOR);
        WOOD_LIST.add(Material.DARK_OAK_TRAPDOOR);
        WOOD_LIST.add(Material.DARK_OAK_LEAVES);
        WOOD_LIST.add(Material.DARK_OAK_SIGN);
        WOOD_LIST.add(Material.DARK_OAK_BOAT);
        WOOD_LIST.add(Material.DARK_OAK_PRESSURE_PLATE);
        WOOD_LIST.add(Material.DARK_OAK_BUTTON);

        /**
         * EARTHEN LIST
         */
        EARTH_LIST.add(Material.ICE); //< Standard earth and building blocks
        EARTH_LIST.add(Material.BLUE_ICE);
        EARTH_LIST.add(Material.MOSSY_STONE_BRICKS);
        EARTH_LIST.add(Material.BRICK);
        EARTH_LIST.add(Material.RED_SAND);
        EARTH_LIST.add(Material.GRAVEL);
        EARTH_LIST.add(Material.COBBLESTONE);
        EARTH_LIST.add(Material.GLASS);
        EARTH_LIST.add(Material.PRISMARINE);
        EARTH_LIST.add(Material.CRACKED_STONE_BRICKS);
        EARTH_LIST.add(Material.MOSSY_COBBLESTONE);
        EARTH_LIST.add(Material.CUT_SANDSTONE);
        EARTH_LIST.add(Material.SAND);
        EARTH_LIST.add(Material.STONE);
        EARTH_LIST.add(Material.GLASS_PANE);
        EARTH_LIST.add(Material.PRISMARINE_BRICKS);
        EARTH_LIST.add(Material.CHISELED_STONE_BRICKS);
        EARTH_LIST.add(Material.SANDSTONE);
        EARTH_LIST.add(Material.POLISHED_GRANITE);
        EARTH_LIST.add(Material.GRANITE);
        EARTH_LIST.add(Material.TERRACOTTA);
        EARTH_LIST.add(Material.DARK_PRISMARINE);
        EARTH_LIST.add(Material.BROWN_MUSHROOM_BLOCK);
        EARTH_LIST.add(Material.CLAY);
        EARTH_LIST.add(Material.CHISELED_SANDSTONE);
        EARTH_LIST.add(Material.POLISHED_DIORITE);
        EARTH_LIST.add(Material.DIORITE);
        EARTH_LIST.add(Material.RED_SANDSTONE);
        EARTH_LIST.add(Material.MUSHROOM_STEM);
        EARTH_LIST.add(Material.PACKED_ICE);
        EARTH_LIST.add(Material.SMOOTH_SANDSTONE);
        EARTH_LIST.add(Material.POLISHED_ANDESITE);
        EARTH_LIST.add(Material.ANDESITE);
        EARTH_LIST.add(Material.CHISELED_RED_SANDSTONE);
        EARTH_LIST.add(Material.RED_MUSHROOM_BLOCK);
        EARTH_LIST.add(Material.SNOW_BLOCK);
        EARTH_LIST.add(Material.SMOOTH_RED_SANDSTONE);
        EARTH_LIST.add(Material.COARSE_DIRT);
        EARTH_LIST.add(Material.DIRT);
        EARTH_LIST.add(Material.CUT_RED_SANDSTONE);
        EARTH_LIST.add(Material.MYCELIUM);
        EARTH_LIST.add(Material.STONE_BRICKS);
        EARTH_LIST.add(Material.SMOOTH_STONE);
        EARTH_LIST.add(Material.PODZOL);
        EARTH_LIST.add(Material.GRASS_BLOCK);
        EARTH_LIST.add(Material.COAL_ORE); //< Ores
        EARTH_LIST.add(Material.IRON_ORE);
        EARTH_LIST.add(Material.GOLD_ORE);
        EARTH_LIST.add(Material.REDSTONE_ORE);
        EARTH_LIST.add(Material.LAPIS_ORE);
        EARTH_LIST.add(Material.DIAMOND_ORE);
        EARTH_LIST.add(Material.EMERALD_ORE);
        EARTH_LIST.add(Material.COAL_BLOCK); //< Valuable blocks
        EARTH_LIST.add(Material.IRON_BLOCK);
        EARTH_LIST.add(Material.GOLD_BLOCK);
        EARTH_LIST.add(Material.REDSTONE_BLOCK);
        EARTH_LIST.add(Material.LAPIS_BLOCK);
        EARTH_LIST.add(Material.DIAMOND_BLOCK);
        EARTH_LIST.add(Material.EMERALD_BLOCK);
        EARTH_LIST.add(Material.COAL); //< Valuable and assorted items
        EARTH_LIST.add(Material.IRON_INGOT);
        EARTH_LIST.add(Material.GOLD_INGOT);
        EARTH_LIST.add(Material.REDSTONE);
        EARTH_LIST.add(Material.LAPIS_LAZULI);
        EARTH_LIST.add(Material.DIAMOND);
        EARTH_LIST.add(Material.EMERALD);
        EARTH_LIST.add(Material.CHARCOAL);
        EARTH_LIST.add(Material.IRON_NUGGET);
        EARTH_LIST.add(Material.GOLD_NUGGET);
        EARTH_LIST.add(Material.FLINT);
        EARTH_LIST.add(Material.END_STONE_BRICK_SLAB); //< Stairs
        EARTH_LIST.add(Material.SMOOTH_SANDSTONE_SLAB);
        EARTH_LIST.add(Material.QUARTZ_SLAB);
        EARTH_LIST.add(Material.GRANITE_SLAB);
        EARTH_LIST.add(Material.ANDESITE_SLAB);
        EARTH_LIST.add(Material.NETHER_BRICK_SLAB);
        EARTH_LIST.add(Material.POLISHED_ANDESITE_SLAB);
        EARTH_LIST.add(Material.PRISMARINE_BRICK_SLAB);
        EARTH_LIST.add(Material.DARK_PRISMARINE_SLAB);
        EARTH_LIST.add(Material.POLISHED_GRANITE_SLAB);
        EARTH_LIST.add(Material.DIORITE_SLAB);
        EARTH_LIST.add(Material.STONE_BRICK_SLAB);
        EARTH_LIST.add(Material.POLISHED_DIORITE_SLAB);
        EARTH_LIST.add(Material.MOSSY_COBBLESTONE_SLAB);
        EARTH_LIST.add(Material.MOSSY_STONE_BRICK_SLAB);
        EARTH_LIST.add(Material.RED_NETHER_BRICK_SLAB);
        EARTH_LIST.add(Material.SMOOTH_QUARTZ_SLAB);
        EARTH_LIST.add(Material.RED_SANDSTONE_SLAB);
        EARTH_LIST.add(Material.SMOOTH_RED_SANDSTONE_SLAB);
        EARTH_LIST.add(Material.PURPUR_SLAB);
        EARTH_LIST.add(Material.PRISMARINE_SLAB);
        EARTH_LIST.add(Material.POLISHED_ANDESITE_STAIRS);
        EARTH_LIST.add(Material.DIORITE_STAIRS);
        EARTH_LIST.add(Material.STONE_SLAB);
        EARTH_LIST.add(Material.SMOOTH_STONE_SLAB);
        EARTH_LIST.add(Material.SANDSTONE_SLAB);
        EARTH_LIST.add(Material.SMOOTH_SANDSTONE_SLAB);
        EARTH_LIST.add(Material.BRICK_SLAB);
        EARTH_LIST.add(Material.MOSSY_COBBLESTONE_STAIRS);
        EARTH_LIST.add(Material.END_STONE_BRICK_STAIRS);
        EARTH_LIST.add(Material.STONE_STAIRS);
        EARTH_LIST.add(Material.SMOOTH_SANDSTONE_STAIRS);
        EARTH_LIST.add(Material.SMOOTH_QUARTZ_STAIRS);
        EARTH_LIST.add(Material.ANDESITE_STAIRS);
        EARTH_LIST.add(Material.RED_NETHER_BRICK_STAIRS);
        EARTH_LIST.add(Material.PRISMARINE_BRICK_STAIRS);
        EARTH_LIST.add(Material.DARK_PRISMARINE_STAIRS);
        EARTH_LIST.add(Material.RED_SANDSTONE_STAIRS);
        EARTH_LIST.add(Material.POLISHED_GRANITE_STAIRS);
        EARTH_LIST.add(Material.SMOOTH_RED_SANDSTONE_STAIRS);
        EARTH_LIST.add(Material.MOSSY_STONE_BRICK_STAIRS);
        EARTH_LIST.add(Material.POLISHED_DIORITE_STAIRS);
        EARTH_LIST.add(Material.PURPUR_STAIRS);
        EARTH_LIST.add(Material.BRICK_STAIRS);
        EARTH_LIST.add(Material.STONE_BRICK_STAIRS);
        EARTH_LIST.add(Material.NETHER_BRICK_STAIRS);
        EARTH_LIST.add(Material.SANDSTONE_STAIRS);
        EARTH_LIST.add(Material.QUARTZ_STAIRS);
        EARTH_LIST.add(Material.PRISMARINE_STAIRS);
        EARTH_LIST.add(Material.RED_NETHER_BRICK_WALL); //< Walls
        EARTH_LIST.add(Material.SANDSTONE_WALL);
        EARTH_LIST.add(Material.END_STONE_BRICK_WALL);
        EARTH_LIST.add(Material.DIORITE_WALL);
        EARTH_LIST.add(Material.MOSSY_STONE_BRICK_WALL);
        EARTH_LIST.add(Material.STONE_BRICK_WALL);
        EARTH_LIST.add(Material.GRANITE_WALL);
        EARTH_LIST.add(Material.NETHER_BRICK_WALL);
        EARTH_LIST.add(Material.ANDESITE_WALL);
        EARTH_LIST.add(Material.COBBLESTONE_WALL);
        EARTH_LIST.add(Material.MOSSY_COBBLESTONE_WALL);
        EARTH_LIST.add(Material.BRICK_WALL);
        EARTH_LIST.add(Material.PRISMARINE_WALL);
        EARTH_LIST.add(Material.RED_SANDSTONE_WALL);
        EARTH_LIST.add(Material.DETECTOR_RAIL); //< Redstone stuff
        EARTH_LIST.add(Material.POWERED_RAIL);
        EARTH_LIST.add(Material.ACTIVATOR_RAIL);
        EARTH_LIST.add(Material.RAIL);
        EARTH_LIST.add(Material.IRON_DOOR);
        EARTH_LIST.add(Material.IRON_TRAPDOOR);
        EARTH_LIST.add(Material.REDSTONE_LAMP);
        EARTH_LIST.add(Material.TRIPWIRE_HOOK);
        EARTH_LIST.add(Material.OBSERVER);
        EARTH_LIST.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        EARTH_LIST.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        EARTH_LIST.add(Material.DAYLIGHT_DETECTOR);
        EARTH_LIST.add(Material.HOPPER);
        EARTH_LIST.add(Material.DROPPER);
        EARTH_LIST.add(Material.DISPENSER);
        EARTH_LIST.add(Material.LEVER);
        EARTH_LIST.add(Material.STONE_BUTTON);
        EARTH_LIST.add(Material.STONE_PRESSURE_PLATE);
        EARTH_LIST.add(Material.REDSTONE_TORCH);
        EARTH_LIST.add(Material.TRAPPED_CHEST);
        EARTH_LIST.add(Material.NOTE_BLOCK);
        EARTH_LIST.add(Material.PISTON);
        EARTH_LIST.add(Material.STICKY_PISTON);
        EARTH_LIST.add(Material.TNT);
        EARTH_LIST.add(Material.RED_BANNER); //< Colored items
        EARTH_LIST.add(Material.RED_BED);
        EARTH_LIST.add(Material.RED_SHULKER_BOX);
        EARTH_LIST.add(Material.RED_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.RED_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.RED_STAINED_GLASS);
        EARTH_LIST.add(Material.RED_CARPET);
        EARTH_LIST.add(Material.RED_TERRACOTTA);
        EARTH_LIST.add(Material.RED_CONCRETE_POWDER);
        EARTH_LIST.add(Material.RED_CONCRETE);
        EARTH_LIST.add(Material.RED_WOOL);
        EARTH_LIST.add(Material.ORANGE_BANNER);
        EARTH_LIST.add(Material.ORANGE_BED);
        EARTH_LIST.add(Material.ORANGE_SHULKER_BOX);
        EARTH_LIST.add(Material.ORANGE_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.ORANGE_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.ORANGE_STAINED_GLASS);
        EARTH_LIST.add(Material.ORANGE_CARPET);
        EARTH_LIST.add(Material.ORANGE_TERRACOTTA);
        EARTH_LIST.add(Material.ORANGE_CONCRETE_POWDER);
        EARTH_LIST.add(Material.ORANGE_CONCRETE);
        EARTH_LIST.add(Material.ORANGE_WOOL);
        EARTH_LIST.add(Material.YELLOW_BANNER);
        EARTH_LIST.add(Material.YELLOW_BED);
        EARTH_LIST.add(Material.YELLOW_SHULKER_BOX);
        EARTH_LIST.add(Material.YELLOW_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.YELLOW_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.YELLOW_STAINED_GLASS);
        EARTH_LIST.add(Material.YELLOW_CARPET);
        EARTH_LIST.add(Material.YELLOW_TERRACOTTA);
        EARTH_LIST.add(Material.YELLOW_CONCRETE_POWDER);
        EARTH_LIST.add(Material.YELLOW_CONCRETE);
        EARTH_LIST.add(Material.YELLOW_WOOL);
        EARTH_LIST.add(Material.LIME_BANNER);
        EARTH_LIST.add(Material.LIME_BED);
        EARTH_LIST.add(Material.LIME_SHULKER_BOX);
        EARTH_LIST.add(Material.LIME_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.LIME_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.LIME_STAINED_GLASS);
        EARTH_LIST.add(Material.LIME_CARPET);
        EARTH_LIST.add(Material.LIME_TERRACOTTA);
        EARTH_LIST.add(Material.LIME_CONCRETE_POWDER);
        EARTH_LIST.add(Material.LIME_CONCRETE);
        EARTH_LIST.add(Material.LIME_WOOL);
        EARTH_LIST.add(Material.GREEN_BANNER);
        EARTH_LIST.add(Material.GREEN_BED);
        EARTH_LIST.add(Material.GREEN_SHULKER_BOX);
        EARTH_LIST.add(Material.GREEN_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.GREEN_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.GREEN_STAINED_GLASS);
        EARTH_LIST.add(Material.GREEN_CARPET);
        EARTH_LIST.add(Material.GREEN_TERRACOTTA);
        EARTH_LIST.add(Material.GREEN_CONCRETE_POWDER);
        EARTH_LIST.add(Material.GREEN_CONCRETE);
        EARTH_LIST.add(Material.GREEN_WOOL);
        EARTH_LIST.add(Material.BLUE_BANNER);
        EARTH_LIST.add(Material.BLUE_BED);
        EARTH_LIST.add(Material.BLUE_SHULKER_BOX);
        EARTH_LIST.add(Material.BLUE_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.BLUE_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.BLUE_STAINED_GLASS);
        EARTH_LIST.add(Material.BLUE_CARPET);
        EARTH_LIST.add(Material.BLUE_TERRACOTTA);
        EARTH_LIST.add(Material.BLUE_CONCRETE_POWDER);
        EARTH_LIST.add(Material.BLUE_CONCRETE);
        EARTH_LIST.add(Material.BLUE_WOOL);
        EARTH_LIST.add(Material.CYAN_BANNER);
        EARTH_LIST.add(Material.CYAN_BED);
        EARTH_LIST.add(Material.CYAN_SHULKER_BOX);
        EARTH_LIST.add(Material.CYAN_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.CYAN_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.CYAN_STAINED_GLASS);
        EARTH_LIST.add(Material.CYAN_CARPET);
        EARTH_LIST.add(Material.CYAN_TERRACOTTA);
        EARTH_LIST.add(Material.CYAN_CONCRETE_POWDER);
        EARTH_LIST.add(Material.CYAN_CONCRETE);
        EARTH_LIST.add(Material.CYAN_WOOL);
        EARTH_LIST.add(Material.LIGHT_BLUE_BANNER);
        EARTH_LIST.add(Material.LIGHT_BLUE_BED);
        EARTH_LIST.add(Material.LIGHT_BLUE_SHULKER_BOX);
        EARTH_LIST.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.LIGHT_BLUE_STAINED_GLASS);
        EARTH_LIST.add(Material.LIGHT_BLUE_CARPET);
        EARTH_LIST.add(Material.LIGHT_BLUE_TERRACOTTA);
        EARTH_LIST.add(Material.LIGHT_BLUE_CONCRETE_POWDER);
        EARTH_LIST.add(Material.LIGHT_BLUE_CONCRETE);
        EARTH_LIST.add(Material.LIGHT_BLUE_WOOL);
        EARTH_LIST.add(Material.PURPLE_BANNER);
        EARTH_LIST.add(Material.PURPLE_BED);
        EARTH_LIST.add(Material.PURPLE_SHULKER_BOX);
        EARTH_LIST.add(Material.PURPLE_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.PURPLE_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.PURPLE_STAINED_GLASS);
        EARTH_LIST.add(Material.PURPLE_CARPET);
        EARTH_LIST.add(Material.PURPLE_TERRACOTTA);
        EARTH_LIST.add(Material.PURPLE_CONCRETE_POWDER);
        EARTH_LIST.add(Material.PURPLE_CONCRETE);
        EARTH_LIST.add(Material.PURPLE_WOOL);
        EARTH_LIST.add(Material.MAGENTA_BANNER);
        EARTH_LIST.add(Material.MAGENTA_BED);
        EARTH_LIST.add(Material.MAGENTA_SHULKER_BOX);
        EARTH_LIST.add(Material.MAGENTA_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.MAGENTA_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.MAGENTA_STAINED_GLASS);
        EARTH_LIST.add(Material.MAGENTA_CARPET);
        EARTH_LIST.add(Material.MAGENTA_TERRACOTTA);
        EARTH_LIST.add(Material.MAGENTA_CONCRETE_POWDER);
        EARTH_LIST.add(Material.MAGENTA_CONCRETE);
        EARTH_LIST.add(Material.MAGENTA_WOOL);
        EARTH_LIST.add(Material.GRAY_BANNER);
        EARTH_LIST.add(Material.GRAY_BED);
        EARTH_LIST.add(Material.GRAY_SHULKER_BOX);
        EARTH_LIST.add(Material.GRAY_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.GRAY_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.GRAY_STAINED_GLASS);
        EARTH_LIST.add(Material.GRAY_CARPET);
        EARTH_LIST.add(Material.GRAY_TERRACOTTA);
        EARTH_LIST.add(Material.GRAY_CONCRETE_POWDER);
        EARTH_LIST.add(Material.GRAY_CONCRETE);
        EARTH_LIST.add(Material.GRAY_WOOL);
        EARTH_LIST.add(Material.LIGHT_GRAY_BANNER);
        EARTH_LIST.add(Material.LIGHT_GRAY_BED);
        EARTH_LIST.add(Material.LIGHT_GRAY_SHULKER_BOX);
        EARTH_LIST.add(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.LIGHT_GRAY_STAINED_GLASS);
        EARTH_LIST.add(Material.LIGHT_GRAY_CARPET);
        EARTH_LIST.add(Material.LIGHT_GRAY_TERRACOTTA);
        EARTH_LIST.add(Material.LIGHT_GRAY_CONCRETE_POWDER);
        EARTH_LIST.add(Material.LIGHT_GRAY_CONCRETE);
        EARTH_LIST.add(Material.LIGHT_GRAY_WOOL);
        EARTH_LIST.add(Material.BROWN_BANNER);
        EARTH_LIST.add(Material.BROWN_BED);
        EARTH_LIST.add(Material.BROWN_SHULKER_BOX);
        EARTH_LIST.add(Material.BROWN_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.BROWN_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.BROWN_STAINED_GLASS);
        EARTH_LIST.add(Material.BROWN_CARPET);
        EARTH_LIST.add(Material.BROWN_TERRACOTTA);
        EARTH_LIST.add(Material.BROWN_CONCRETE_POWDER);
        EARTH_LIST.add(Material.BROWN_CONCRETE);
        EARTH_LIST.add(Material.BROWN_WOOL);
        EARTH_LIST.add(Material.BLACK_BANNER);
        EARTH_LIST.add(Material.BLACK_BED);
        EARTH_LIST.add(Material.BLACK_SHULKER_BOX);
        EARTH_LIST.add(Material.BLACK_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.BLACK_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.BLACK_STAINED_GLASS);
        EARTH_LIST.add(Material.BLACK_CARPET);
        EARTH_LIST.add(Material.BLACK_TERRACOTTA);
        EARTH_LIST.add(Material.BLACK_CONCRETE_POWDER);
        EARTH_LIST.add(Material.BLACK_CONCRETE);
        EARTH_LIST.add(Material.BLACK_WOOL);
        EARTH_LIST.add(Material.WHITE_BANNER);
        EARTH_LIST.add(Material.WHITE_BED);
        EARTH_LIST.add(Material.WHITE_SHULKER_BOX);
        EARTH_LIST.add(Material.WHITE_GLAZED_TERRACOTTA);
        EARTH_LIST.add(Material.WHITE_STAINED_GLASS_PANE);
        EARTH_LIST.add(Material.WHITE_STAINED_GLASS);
        EARTH_LIST.add(Material.WHITE_CARPET);
        EARTH_LIST.add(Material.WHITE_TERRACOTTA);
        EARTH_LIST.add(Material.WHITE_CONCRETE_POWDER);
        EARTH_LIST.add(Material.WHITE_CONCRETE);
        EARTH_LIST.add(Material.WHITE_WOOL);
        EARTH_LIST.add(Material.AZURE_BLUET); //< Flowers
        EARTH_LIST.add(Material.ALLIUM);
        EARTH_LIST.add(Material.POPPY);
        EARTH_LIST.add(Material.DANDELION);
        EARTH_LIST.add(Material.SEA_PICKLE);
        EARTH_LIST.add(Material.BLUE_ORCHID);
        EARTH_LIST.add(Material.SEAGRASS);
        EARTH_LIST.add(Material.DEAD_BUSH);
        EARTH_LIST.add(Material.FERN);
        EARTH_LIST.add(Material.LILY_OF_THE_VALLEY);
        EARTH_LIST.add(Material.CORNFLOWER);
        EARTH_LIST.add(Material.OXEYE_DAISY);
        EARTH_LIST.add(Material.COBWEB);
        EARTH_LIST.add(Material.WHITE_TULIP);
        EARTH_LIST.add(Material.ORANGE_TULIP);
        EARTH_LIST.add(Material.RED_TULIP);
        EARTH_LIST.add(Material.GRASS);
        EARTH_LIST.add(Material.BROWN_MUSHROOM);
        EARTH_LIST.add(Material.RED_MUSHROOM);
        EARTH_LIST.add(Material.BOOKSHELF); //< Randoms
        EARTH_LIST.add(Material.PUMPKIN);
        EARTH_LIST.add(Material.JACK_O_LANTERN);
        EARTH_LIST.add(Material.LADDER);
        EARTH_LIST.add(Material.FURNACE);
        EARTH_LIST.add(Material.CRAFTING_TABLE);
        EARTH_LIST.add(Material.CHORUS_FLOWER);
        EARTH_LIST.add(Material.END_ROD);
        EARTH_LIST.add(Material.TORCH);
        EARTH_LIST.add(Material.CACTUS);
        EARTH_LIST.add(Material.IRON_BARS);
        EARTH_LIST.add(Material.MELON);
        EARTH_LIST.add(Material.VINE);
        EARTH_LIST.add(Material.LILY_PAD);
        EARTH_LIST.add(Material.ENCHANTING_TABLE);
        EARTH_LIST.add(Material.JUKEBOX);
        EARTH_LIST.add(Material.ANVIL);
        EARTH_LIST.add(Material.DAMAGED_ANVIL);
        EARTH_LIST.add(Material.BEACON);
        EARTH_LIST.add(Material.HAY_BLOCK);
        EARTH_LIST.add(Material.SLIME_BLOCK);
        EARTH_LIST.add(Material.SUNFLOWER);
        EARTH_LIST.add(Material.LILAC);
        EARTH_LIST.add(Material.ROSE_BUSH);
        EARTH_LIST.add(Material.PEONY);
        EARTH_LIST.add(Material.TALL_GRASS);
        EARTH_LIST.add(Material.LARGE_FERN);
        EARTH_LIST.add(Material.BONE_BLOCK);
        EARTH_LIST.add(Material.SHULKER_BOX);
        EARTH_LIST.add(Material.DEAD_BRAIN_CORAL_BLOCK); //< Coral
        EARTH_LIST.add(Material.DEAD_BUBBLE_CORAL_BLOCK);
        EARTH_LIST.add(Material.DEAD_HORN_CORAL_BLOCK);
        EARTH_LIST.add(Material.DEAD_FIRE_CORAL_BLOCK);
        EARTH_LIST.add(Material.DEAD_TUBE_CORAL_BLOCK);
        EARTH_LIST.add(Material.BRAIN_CORAL_BLOCK);
        EARTH_LIST.add(Material.BUBBLE_CORAL_BLOCK);
        EARTH_LIST.add(Material.HORN_CORAL_BLOCK);
        EARTH_LIST.add(Material.FIRE_CORAL_BLOCK);
        EARTH_LIST.add(Material.TUBE_CORAL_BLOCK);
        EARTH_LIST.add(Material.BRAIN_CORAL);
        EARTH_LIST.add(Material.BUBBLE_CORAL);
        EARTH_LIST.add(Material.HORN_CORAL);
        EARTH_LIST.add(Material.FIRE_CORAL);
        EARTH_LIST.add(Material.TUBE_CORAL);
        EARTH_LIST.add(Material.BRAIN_CORAL_FAN);
        EARTH_LIST.add(Material.BUBBLE_CORAL_FAN);
        EARTH_LIST.add(Material.HORN_CORAL_FAN);
        EARTH_LIST.add(Material.FIRE_CORAL_FAN);
        EARTH_LIST.add(Material.TUBE_CORAL_FAN);
        EARTH_LIST.add(Material.DEAD_BRAIN_CORAL);
        EARTH_LIST.add(Material.DEAD_BUBBLE_CORAL);
        EARTH_LIST.add(Material.DEAD_HORN_CORAL);
        EARTH_LIST.add(Material.DEAD_FIRE_CORAL);
        EARTH_LIST.add(Material.DEAD_TUBE_CORAL);
        EARTH_LIST.add(Material.DEAD_BRAIN_CORAL_FAN);
        EARTH_LIST.add(Material.DEAD_BUBBLE_CORAL_FAN);
        EARTH_LIST.add(Material.DEAD_HORN_CORAL_FAN);
        EARTH_LIST.add(Material.DEAD_FIRE_CORAL_FAN);
        EARTH_LIST.add(Material.DEAD_TUBE_CORAL_FAN);
        EARTH_LIST.add(Material.DEAD_TUBE_CORAL_FAN);
        EARTH_LIST.add(Material.TURTLE_EGG);
        EARTH_LIST.add(Material.SCAFFOLDING);
        EARTH_LIST.add(Material.CONDUIT);

        /**
         * NETHER END LIST
         */
        NETHER_END_LIST.add(Material.SPONGE);
        NETHER_END_LIST.add(Material.WET_SPONGE);
        NETHER_END_LIST.add(Material.MAGMA_BLOCK);
        NETHER_END_LIST.add(Material.PURPUR_BLOCK);
        NETHER_END_LIST.add(Material.PURPUR_PILLAR);
        NETHER_END_LIST.add(Material.NETHERRACK);
        NETHER_END_LIST.add(Material.SOUL_SAND);
        NETHER_END_LIST.add(Material.GLOWSTONE);
        NETHER_END_LIST.add(Material.END_STONE);
        NETHER_END_LIST.add(Material.END_STONE_BRICKS);
        NETHER_END_LIST.add(Material.RED_NETHER_BRICKS);
        NETHER_END_LIST.add(Material.QUARTZ_BLOCK);
        NETHER_END_LIST.add(Material.QUARTZ_BRICKS);
        NETHER_END_LIST.add(Material.QUARTZ_PILLAR);
        NETHER_END_LIST.add(Material.NETHER_BRICK);
        NETHER_END_LIST.add(Material.NETHER_WART_BLOCK);
        NETHER_END_LIST.add(Material.NETHER_QUARTZ_ORE);
        NETHER_END_LIST.add(Material.QUARTZ);
        NETHER_END_LIST.add(Material.OBSIDIAN);
        NETHER_END_LIST.add(Material.NETHER_BRICK_FENCE);
    }
}
