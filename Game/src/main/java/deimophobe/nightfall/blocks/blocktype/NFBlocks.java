package deimophobe.nightfall.blocks.blocktype;

import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.dwarf.kit.hero.Trident;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.RedstoneRail;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class NFBlocks {
	private NFBlocks() {}
	
	// ===== Simple Minecraft Groups =====
	public static final BlockInteracter AIR = new MaterialBlock(Material.AIR);
	
	public static final BlockMatcher WATER = new MaterialSet(Material.WATER);
	public static final BlockMatcher LAVA = new MaterialSet(Material.LAVA);
	
	public static final BlockMatcher LIQUID = Block::isLiquid;
	public static final BlockMatcher SOLID = block -> block.getType().isSolid();
	
	public static final BlockMatcher GRASS_BLOCK = new MaterialSet(Material.GRASS, Material.MYCELIUM);
	public static final BlockInteracter DIRT = new MaterialBlock(Material.COARSE_DIRT);
	public static final BlockInteracter PODZOL = new MaterialBlock(Material.PODZOL);
	
	public static final BlockMatcher LOG = new BlockSet(
			RepeatMaterial.woodMaterial("log"),
			RepeatMaterial.woodMaterial("stripped_","log")
	);
	public static final BlockInteracter LEAVES = RepeatMaterial.woodMaterial("leaves");
	public static final BlockMatcher DOOR = RepeatMaterial.woodMaterial("door").butAlso(Material.IRON_DOOR);
	
	public static final BlockMatcher SHULKER_BOX = RepeatMaterial.colourMaterial("shulker-box");
	public static final BlockMatcher BED = RepeatMaterial.colourMaterial("bed");
	public static final BlockMatcher WOOL = RepeatMaterial.colourMaterial("wool");
	public static final BlockMatcher CARPET = RepeatMaterial.colourMaterial("carpet");
	
	// ===== Other Minecraft Groups =====
	public static final BlockMatcher GLASS = new BlockSet(
			RepeatMaterial.colourMaterial("stained-glass"),
			RepeatMaterial.colourMaterial("stained-glass-pane")
	).butAlso(
			Material.GLASS,
			Material.GLASS_PANE
	);
	
	public static final BlockMatcher IGNITEABLE = new BlockMatcher() {
		@Override
		public boolean matchesBlock(@NotNull Block block) {
			if (!IGNORABLE.matchesBlock(block)) return false;
			
			Block below = block.getRelative(0,-1,0);
			if (!SOLID.matchesBlock(below)) return false;
			
			return true;
		}
	};
	
	
	public static final BlockMatcher GRASS = new MaterialSet(
			Material.GRASS,
			Material.TALL_GRASS,
			Material.FERN,
			Material.LARGE_FERN,
			Material.DEAD_BUSH,
			
			Material.SEAGRASS,
			Material.TALL_SEAGRASS
	);
	public static final BlockMatcher DECORATIVES = new BlockSet(
			GRASS,
			
			RepeatMaterial.coralMaterial("", "coral"),
			RepeatMaterial.coralMaterial("", "coral-fan"),
			RepeatMaterial.coralMaterial("dead_", "coral"),
			RepeatMaterial.coralMaterial("dead_", "coral-fan")
	).orOfMaterial(
			Material.DANDELION,
			Material.POPPY,
			Material.BLUE_ORCHID,
			Material.ALLIUM,
			Material.AZURE_BLUET,
			Material.RED_TULIP,
			Material.ORANGE_TULIP,
			Material.WHITE_TULIP,
			Material.PINK_TULIP,
			Material.OXEYE_DAISY,
			
			Material.BROWN_MUSHROOM,
			Material.RED_MUSHROOM,
			
			Material.SUNFLOWER,
			Material.LILAC,
			Material.ROSE_BUSH,
			Material.PEONY,
			
			Material.SEA_PICKLE,
			Material.VINE,
			Material.LILY_PAD
	);
	
	
	
	// ===== Defenses =====
	
	// Walls
	public static final BlockInteracter ENCHANTED_WALL = new MaterialBlock(Material.LAPIS_ORE);
	public static final BlockInteracter NORMAL_WALL = new MaterialSet(Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CHISELED_STONE_BRICKS);
	public static final BlockInteracter CRACKED_WALL = new MaterialBlock(Material.CRACKED_STONE_BRICKS);
	public static final BlockInteracter DAMAGED_WALL = new MaterialSet(Material.COBBLESTONE, Material.MOSSY_COBBLESTONE);
	public static final BlockInteracter BROKEN_WALL = new MaterialBlock(Material.GRAVEL);
	
	public static final BlockInteracter CORRODED_WALL = new MaterialBlock(Material.SLIME_BLOCK);
	
	public static final BlockMatcher MORTARABLE_WALL = new BlockSet(NORMAL_WALL, DAMAGED_WALL, new MaterialBlock(Material.BRICK));
	public static final BlockMatcher WALL = new BlockSet(ENCHANTED_WALL, MORTARABLE_WALL);
	
	// Stairs
	public static final BlockInteracter NORMAL_STAIR = DataConverter.stairInteracter(Material.STONE_BRICK_STAIRS);
	public static final BlockInteracter DAMAGED_STAIR = DataConverter.stairInteracter(Material.COBBLESTONE_STAIRS);
	public static final BlockMatcher ALL_STAIRS = new BlockSet(NORMAL_STAIR, DAMAGED_STAIR);
	
	// Slabs
	public static final BlockInteracter REINFORCED_SLAB = DataConverter.slabInteracter(Material.STONE_SLAB);
	public static final BlockInteracter NORMAL_SLAB = DataConverter.slabInteracter(Material.STONE_BRICK_SLAB);
	public static final BlockInteracter DAMAGED_SLAB = DataConverter.slabInteracter(Material.COBBLESTONE_SLAB);
	public static final BlockMatcher ALL_SLABS = new BlockSet(REINFORCED_SLAB, NORMAL_SLAB, DAMAGED_SLAB);
	
	// Light
	public static final BlockMatcher EXPLODEABLE_LIGHT = new MaterialSet(Material.REDSTONE_LAMP, Material.GLOWSTONE, Material.SEA_LANTERN, Material.END_ROD);
	public static final BlockMatcher TORCH = new MaterialSet(Material.TORCH, Material.WALL_TORCH);
	public static final BlockMatcher LIGHT = EXPLODEABLE_LIGHT.or(TORCH);
	
	
	
	// ===== Dwarf Stuff =====
	
	// Gold
	public static final BlockInteracter CRACKED_GOLD_1 = new ArmourBlockInteracter(Material.YELLOW_WOOL);
	public static final BlockInteracter CRACKED_GOLD_2 = new ArmourBlockInteracter(Material.ORANGE_WOOL);
	public static final BlockInteracter CRACKED_GOLD_3 = new ArmourBlockInteracter(Material.MAGENTA_WOOL);
	public static final BlockInteracter REFINED_GOLD = new MaterialBlock(Material.GOLD_BLOCK);
	public static final BlockMatcher CRAFTING_ARMOUR = new BlockSet(CRACKED_GOLD_1, CRACKED_GOLD_2, CRACKED_GOLD_3);
	public static final BlockMatcher PISTON_BASE = new MaterialSet(Material.PISTON, Material.MOVING_PISTON);
	
	// Crafting
	public static final BlockInteracter OIL = new MaterialBlock(Material.SPONGE);
	public static final BlockInteracter SAWS = new MaterialBlock(Material.IRON_BARS);
	public static final BlockInteracter ANVIL = new MaterialBlock(Material.ANVIL);
	public static final BlockMatcher FURNACE = new MaterialSet(Material.FURNACE);
	public static final BlockMatcher SHARED_CHEST = new MaterialSet(Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST);
	public static final BlockMatcher DIGGING_SAND = RepeatMaterial.colourMaterial("concrete-powder").butAlso(Material.SAND);
	
	// Shrine
	public static final BlockMatcher SHRINE_BLOCK = new MaterialBlock(Material.END_PORTAL_FRAME);
	public static final BlockMatcher ACTIVE_SHRINE_BLOCK = SHRINE_BLOCK.butAlso(
			block -> GameMap.getCurrentMap().getCurrentShrineRegion().containsBlock(block)
	);
	
	
	// Tools
	public static final BlockMatcher PICKUP_SWORD = new MaterialBlock(Material.LADDER);
	public static final BlockMatcher PICKUP_BOW = new DataChecker<>(Material.DETECTOR_RAIL, RedstoneRail.class, rail -> !rail.isPowered());
	public static final BlockMatcher PICKUP_ALE = new MaterialSet(Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH);
	public static final BlockMatcher PICKUP_PICK = new DataChecker<>(Material.ACTIVATOR_RAIL, RedstoneRail.class, rail -> !rail.isPowered());
	public static final BlockMatcher PICKUP_AXE = new DataChecker<>(Material.RAIL, Rail.class, rail -> Util.isRailStraight(rail.getShape()));
	public static final BlockMatcher PICKUP_SHOVEL = new DataChecker<>(Material.POWERED_RAIL, RedstoneRail.class, rail -> !rail.isPowered());
	public static final BlockMatcher PICKUP_BLOCK = new BlockSet(
			PICKUP_SWORD, PICKUP_BOW, PICKUP_ALE, PICKUP_PICK, PICKUP_AXE, PICKUP_SHOVEL
	);
	
	
	
	// ===== Miscellaneous =====
	public static final BlockMatcher HERANA_TRIDENT_WATER = Trident::isTridentWaterBlock;
	public static final BlockMatcher TIMED_BLOCK = block -> BlockManager.getManager().isTimedBlock(block);
	
	
	
	// ===== Block Sets =====
	//TODO organise
	public static final BlockMatcher ALWAYS_BREAKABLE = new BlockSet(
			LIGHT,
			DECORATIVES,
			TORCH
	).except(TIMED_BLOCK);
	
	public static final BlockMatcher UNTIMEABLE_BLOCKS = new BlockSet(
			PISTON_BASE,
			CRAFTING_ARMOUR,
			PICKUP_BLOCK,
			
			SHRINE_BLOCK,
			SHARED_CHEST,
			
			HERANA_TRIDENT_WATER,
			
			BED,
			DOOR
			
	).orOfMaterial(
			Material.AIR,
			
			Material.SPONGE,
			Material.IRON_BARS,
			
			Material.JACK_O_LANTERN,
			Material.PURPUR_BLOCK,
			
			Material.BEACON,
			
			Material.IRON_DOOR,

			Material.GOLD_ORE
	);
	
	public static final BlockMatcher UNPLACEABLE_BLOCKS = new BlockSet(
			HERANA_TRIDENT_WATER,
			TIMED_BLOCK
	).orOfMaterial(
			Material.BARRIER,
			Material.DISPENSER,
			Material.CHEST
	);
	
	public static final BlockMatcher UNBREAKABLE_BLOCKS = new BlockSet(
			UNTIMEABLE_BLOCKS.except(Material.GOLD_ORE),
			TIMED_BLOCK,
			LOG
			
	).orOfMaterial(
			Material.IRON_BLOCK,
			
			Material.SIGN,
			Material.WALL_SIGN,
			Material.ICE,
			Material.FROSTED_ICE,
			Material.PACKED_ICE,
			
			Material.REDSTONE_BLOCK
	);
	
	
	public static final BlockMatcher UNINTERACTABLE_BLOCKS = new BlockSet(
			SHULKER_BOX,
			SHARED_CHEST,
			
			BED
	).orOfMaterial(
			Material.HOPPER,
			Material.CRAFTING_TABLE,
			Material.FURNACE,
			Material.ANVIL,
			Material.BREWING_STAND,
			Material.DISPENSER,
			Material.DROPPER,
			Material.BEACON,
			Material.ENCHANTING_TABLE,
			Material.COMMAND_BLOCK,
			Material.CHAIN_COMMAND_BLOCK,
			Material.REPEATING_COMMAND_BLOCK,
			Material.FLOWER_POT
	);

	public static final BlockMatcher EMPTY_BLOCKS = DECORATIVES
			.orOfMaterial(Material.AIR, Material.FIRE)
			.except(TIMED_BLOCK);
	
	public static final BlockMatcher IGNORABLE = new BlockSet(
			EMPTY_BLOCKS,
			CARPET
	).orOfMaterial(
			Material.SNOW
	);

	public static final BlockMatcher SLABBABLE = new BlockSet(
			NORMAL_WALL,
			CRACKED_WALL,
			DAMAGED_WALL,
			BROKEN_WALL,
			IGNORABLE
	).orOfMaterial(Material.FIRE);



	public static boolean tryConvertBlock(Block block, BlockMatcher from, BlockPlacer to) {
		boolean matches = from.matchesBlock(block);
		if (!matches) return false;
		
		to.setAtBlock(block);
		return true;
	}
}
