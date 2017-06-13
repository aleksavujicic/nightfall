package deimophobe.dvz.blocks;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.shrine.region.Region;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 9/02/17.
 */
public class BlockManager {
	private static BlockManager ourManager = new BlockManager();
	public static BlockManager getManager() {
		return ourManager;
	}
	private BlockManager() {}
	
	
	
	// --------------------------------------------------------
	//                      UNBREAKABLES
	// --------------------------------------------------------
	
	private static final Material[] FIXED_BLOCKS = {
			Material.LOG,
			Material.LOG_2,
			Material.SPONGE,
			Material.IRON_FENCE,
			Material.JACK_O_LANTERN,
			Material.RAILS,
			Material.ACTIVATOR_RAIL,
			Material.DETECTOR_RAIL,
			Material.POWERED_RAIL,
			Material.LADDER,
			Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.PISTON_BASE,
			Material.PISTON_EXTENSION,
			Material.PISTON_STICKY_BASE,
			Material.PISTON_MOVING_PIECE,
			Material.IRON_BLOCK,
			Material.SIGN,
			Material.SIGN_POST,
			Material.WALL_SIGN,
			Material.CHEST,
			Material.TRAPPED_CHEST,
	};
	
	private static final Set<Region> unbreakableRegions = new HashSet<>();
	
	public void addUnbreakableRegion(Region region) {
		unbreakableRegions.add(region);
	}
	
	public boolean isBreakable(Block block) {
		if (block == null) return false;
		
		Material blockType = block.getType();
		for (Material material : FIXED_BLOCKS) {
			if (material == blockType)
				return false;
		}
		
		for (Region region : unbreakableRegions) {
			if (region.containsBlock(block))
				return false;
		}
		
		return true;
	}
	
	public boolean breakBlockEvent(GamePlayer player, Block block) {
		Material blockType = block.getType();
		
		player.onBlockBreak(block);
		
		return isBreakable(block);
	}
	
	
	
	// --------------------------------------------------------
	//                      WALL MAGIC
	// --------------------------------------------------------
	
	private static final double CORRODE_CHANCE = 0.1;
	private final static Material[] CORRODABLE = {
			//Material.LAPIS_ORE,
			Material.SMOOTH_BRICK,
			Material.MOSSY_COBBLESTONE,
			Material.COBBLESTONE
	};
	public void corrodeBlocks(Block center) {
		
	}
	
	
	private static final double BLUE_WALL_MORTAR_CHANCE = 0.03;
	public boolean mortarWalls(Block center, boolean wizzy) {
		Location loc = center.getLocation();
		World world = loc.getWorld();
		
		int x_center = loc.getBlockX();
		int y_center = loc.getBlockY();
		int z_center = loc.getBlockZ();
		
		final int x_size = 4; // actual size is double this plus 1 (so 9)
		final int y_size = 3; // actual size 7
		final int z_size = 4; // actual size 9
		
		boolean used = false;
		boolean forceBlue = wizzy || Game.getGame().getPhase() == Phase.BUILD || Game.getGame().getPhase() == Phase.PLAGUE;
		for (int x = x_center - x_size; x <= x_center + x_size; x++) {
			for (int y = y_center - y_size; y <= y_center + y_size; y++) {
				for (int z = z_center - z_size; z <= z_center + z_size; z++) {
					Block toReplace = world.getBlockAt(x,y,z);
					if (isWallBlock(toReplace.getType())) {
						if (forceBlue || Math.random() <= BLUE_WALL_MORTAR_CHANCE) {
							toReplace.setType(Material.LAPIS_ORE);
						} else {
							toReplace.setType(Material.SMOOTH_BRICK);
						}
						used = true;
					}
				}
			}
		}
		return used;
	}
	
	private final static Material[] MORTARABLE = {
			//Material.LAPIS_ORE,
			Material.SMOOTH_BRICK,
			Material.MOSSY_COBBLESTONE,
			Material.COBBLESTONE
	};
	private static boolean isWallBlock(Material material) {
		for (Material wallBlock : MORTARABLE) {
			if (wallBlock == material)
				return true;
		}
		return false;
	}
	
	
	// --------------------------------------------------------
	//                        EXPLOSIONS
	// --------------------------------------------------------
}
