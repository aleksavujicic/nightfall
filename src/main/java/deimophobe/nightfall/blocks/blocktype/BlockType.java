package deimophobe.nightfall.blocks.blocktype;

import deimophobe.nightfall.dwarf.kit.hero.Trident;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class BlockType {
	public static final CustomBlock AIR = new MaterialBlock(Material.AIR);
	
	
	// ----- GOLD MAKING -----
	public static final CustomBlock CRACKED_GOLD_1 = new WoolBlock(DyeColor.YELLOW);
	public static final CustomBlock CRACKED_GOLD_2 = new WoolBlock(DyeColor.ORANGE);
	public static final CustomBlock CRACKED_GOLD_3 = new WoolBlock(DyeColor.MAGENTA);
	public static final CustomBlock REFINED_GOLD = new MaterialBlock(Material.GOLD_BLOCK);
	public static final ComparableBlock PISTON_BASE = new MaterialSet(Material.PISTON_EXTENSION, Material.PISTON_BASE);
	
	
	// ----- WALLS N STUFF -----
	public static final CustomBlock ENCHANTED_WALL = new MaterialBlock(Material.LAPIS_ORE);
	public static final CustomBlock NORMAL_WALL = new DataBlock(Material.SMOOTH_BRICK, (byte) 0);
	public static final CustomBlock MOSSY_WALL = new DataBlock(Material.SMOOTH_BRICK, (byte) 1);
	public static final CustomBlock CRACKED_WALL = new DataBlock(Material.SMOOTH_BRICK, (byte) 2);
	public static final CustomBlock CHISELED_WALL = new DataBlock(Material.SMOOTH_BRICK, (byte) 3);
	public static final CustomBlock DAMAGED_WALL = new MaterialSet(Material.COBBLESTONE, Material.MOSSY_COBBLESTONE);
	public static final CustomBlock BROKEN_WALL = new MaterialBlock(Material.GRAVEL);
	
	public static final CustomBlock CORRODED_WALL = new WoolBlock(DyeColor.LIME);
	
	public static final ComparableBlock MORTARABLE_WALL = new BlockSet(NORMAL_WALL, CRACKED_WALL, DAMAGED_WALL, new MaterialBlock(Material.SMOOTH_BRICK));
	public static final ComparableBlock WALL = new BlockSet(ENCHANTED_WALL, MORTARABLE_WALL);
	
	
	public static final CustomBlock NORMAL_STAIR = new StairBlock(Material.SMOOTH_STAIRS);
	public static final CustomBlock DAMAGED_STAIR = new StairBlock(Material.COBBLESTONE_STAIRS);
	public static final ComparableBlock ALL_STAIRS = new BlockSet(NORMAL_STAIR, DAMAGED_STAIR);
	
	public static final CustomBlock REINFORCED_SLAB = new SlabBlock(Material.STONE);
	public static final CustomBlock NORMAL_SLAB = new SlabBlock(Material.SMOOTH_BRICK);
	public static final CustomBlock DAMAGED_SLAB = new SlabBlock(Material.COBBLESTONE);
	public static final ComparableBlock ALL_SLABS = new BlockSet(REINFORCED_SLAB, NORMAL_SLAB, DAMAGED_SLAB);
	
	
	public static final ComparableBlock ALL_WOOLS = new MaterialBlock(Material.WOOL);
	
	// ----- LIGHT -----
	public static final ComparableBlock REDSTONE_LAMP = new MaterialSet(Material.REDSTONE_LAMP_OFF, Material.REDSTONE_LAMP_ON);
	public static final CustomBlock GLOWSTONE = new MaterialBlock(Material.GLOWSTONE);
	public static final CustomBlock SEA_LANTERN = new MaterialBlock(Material.SEA_LANTERN);
	public static final CustomBlock END_ROD = new MaterialBlock(Material.END_ROD);
	public static final ComparableBlock LIGHT = new BlockSet(REDSTONE_LAMP, GLOWSTONE, SEA_LANTERN, END_ROD);
	
	
	
	// ----- MISC -----
	public static final ComparableBlock GRASS = new MaterialSet(
			Material.DOUBLE_PLANT, Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.DEAD_BUSH
	);
	public static final ComparableBlock GRASS_BLOCK = new MaterialSet(Material.GRASS, Material.MYCEL);
	public static final CustomBlock DIRT_BLOCK = new DataBlock(Material.DIRT, (byte) 1);
	public static final CustomBlock PODZOL_BLOCK = new DataBlock(Material.DIRT, (byte) 2);
	
	public static final ComparableBlock ACTIVE_SHRINE_BLOCK =
			block -> (
					block.getType() == Material.ENDER_PORTAL_FRAME
					&& GameMap.getCurrentMap().getCurrentShrineRegion().containsBlock(block)
			);
	public static final ComparableBlock ANY_SHRINE_BLOCK = new MaterialBlock(Material.ENDER_PORTAL_FRAME);
	
	public static final ComparableBlock SHARED_CHEST = new MaterialSet(Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST);
	
	public static final ComparableBlock HERANA_TRIDENT_WATER = Trident::isTridentWaterBlock;
	
	// Chisel
	public static final CustomBlock COBBLESTONE_BLOCK = new MaterialBlock(Material.COBBLESTONE);
	public static final StairBlock COBBLESTONE_STAIR = new StairBlock(Material.COBBLESTONE);
	public static final SlabBlock COBBLESTONE_SLAB = new SlabBlock(Material.COBBLESTONE);
	
	
	// ----- Blocksets -----
	public static final ComparableBlock ALWAYS_BREAKABLE = new BlockSet(
			LIGHT,
			
			new MaterialSet(
					Material.TORCH,
					
					Material.DOUBLE_PLANT,
					Material.LONG_GRASS,
					Material.YELLOW_FLOWER,
					Material.RED_ROSE
			)
	);
	
	public static final ComparableBlock UNTIMEABLE_BLOCKS = new BlockSet(
			PISTON_BASE,
			CRACKED_GOLD_1,
			CRACKED_GOLD_2,
			CRACKED_GOLD_3,
			
			ANY_SHRINE_BLOCK,
			SHARED_CHEST,
			
			HERANA_TRIDENT_WATER,
			
			new MaterialSet(
					Material.AIR,
					
					Material.SPONGE,
					Material.IRON_FENCE,
					
					Material.RAILS,
					Material.ACTIVATOR_RAIL,
					Material.DETECTOR_RAIL,
					Material.POWERED_RAIL,
					Material.LADDER,
					Material.REDSTONE_TORCH_ON,
					Material.REDSTONE_TORCH_OFF,
					
					Material.JACK_O_LANTERN,
					Material.PURPUR_BLOCK,
					
					Material.BEACON,
					
					Material.BED_BLOCK,
					Material.DARK_OAK_DOOR,
					Material.ACACIA_DOOR,
					Material.BIRCH_DOOR,
					Material.IRON_DOOR,
					Material.JUNGLE_DOOR,
					Material.SPRUCE_DOOR,
					Material.WOOD_DOOR,
					Material.WOODEN_DOOR,

					Material.GOLD_ORE
			)
	);
	
	public static final ComparableBlock UNPLACEABLE_BLOCKS = new MaterialSet(
			Material.BARRIER,
			Material.DISPENSER,
			Material.CHEST
	);
	
	public static final ComparableBlock UNBREAKABLE_BLOCKS = new BlockSet(
			UNTIMEABLE_BLOCKS.except(Material.GOLD_ORE),
			
			new MaterialSet(
					Material.LOG,
					Material.LOG_2,
					
					Material.IRON_BLOCK,
					
					Material.SIGN,
					Material.SIGN_POST,
					Material.WALL_SIGN,
					Material.ICE,
					Material.FROSTED_ICE,
					Material.PACKED_ICE,
					
					Material.REDSTONE_BLOCK
			)
	);
	
	public static final ComparableBlock GOLEM_UNBREAKABLE_BLOCKS = new BlockSet(
			UNBREAKABLE_BLOCKS,
			
			Block::isLiquid,
			
			new MaterialSet(
					Material.AIR,
					Material.BARRIER,
					Material.BEDROCK
			)
	);
	
	public static final ComparableBlock SHULKER_BOX = block -> (block.getState() instanceof ShulkerBox);
	public static final ComparableBlock UNINTERACTABLE_BLOCKS = new BlockSet(
			SHULKER_BOX,
			SHARED_CHEST,
			new MaterialSet(
					Material.BED,
					Material.BED_BLOCK,
					Material.HOPPER,
					Material.WORKBENCH,
					Material.FURNACE,
					Material.ANVIL,
					Material.BREWING_STAND,
					Material.DISPENSER,
					Material.DROPPER,
					Material.BEACON,
					Material.ENCHANTMENT_TABLE,
					Material.COMMAND,
					Material.COMMAND_CHAIN,
					Material.COMMAND_MINECART,
					Material.COMMAND_REPEATING
			)
	);

	public static final ComparableBlock PLANTS = new BlockSet(
			new MaterialSet(
					Material.DOUBLE_PLANT,
					Material.LONG_GRASS,
					Material.YELLOW_FLOWER,
					Material.RED_ROSE
			)
	);

	public static final ComparableBlock EMPTY_BLOCKS = new BlockSet(
			PLANTS,
			new MaterialSet(
					Material.AIR,
					Material.FIRE
			)
	);

	public static final ComparableBlock IGNORABLE = new BlockSet(
			EMPTY_BLOCKS,
			PLANTS,
			new MaterialSet(
					Material.SNOW,
					Material.CARPET
			)
	);

	public static final ComparableBlock SLABBABLE = new BlockSet(
			NORMAL_WALL,
			CRACKED_WALL,
			DAMAGED_WALL,
			BROKEN_WALL,
			ALL_SLABS,
			ALL_STAIRS,
			IGNORABLE,
			new MaterialSet(Material.SMOOTH_BRICK, Material.FIRE)
	);



	public static boolean tryConvertBlock(Block block, ComparableBlock from, SettableBlock to) {
		boolean matches = from.matchesBlock(block);
		if (!matches) return false;
		
		to.setAtBlock(block);
		return true;
	}
}
