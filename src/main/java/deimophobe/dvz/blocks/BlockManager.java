package deimophobe.dvz.blocks;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.Phase;
import deimophobe.dvz.blocks.blocktype.BlockType;
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
	
	private static final Set<Region> unbreakableRegions = new HashSet<>();
	
	public void addUnbreakableRegion(Region region) {
		unbreakableRegions.add(region);
	}
	
	public boolean isBreakable(Block block) {
		if (block == null) return false;
		
		if (BlockType.UNBREAKABLE_BLOCKS.matchesBlock(block))
			return false;
		
		for (Region region : unbreakableRegions) {
			if (region.containsBlock(block))
				return false;
		}
		
		return true;
	}
	
	public boolean breakBlockEvent(GamePlayer player, Block block) {
		if (player != null)
			player.onBlockBreak(block);
		
		return isBreakable(block);
	}
	
	
	
	// --------------------------------------------------------
	//                      WALL MAGIC
	// --------------------------------------------------------
	
	
	/*
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
	*/
	
	
	// --------------------------------------------------------
	//                        EXPLOSIONS
	// --------------------------------------------------------
}
