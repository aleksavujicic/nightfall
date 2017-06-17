package deimophobe.dvz.blocks;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class BlockType {
	public static final CustomBlock AIR = new SimpleBlock(Material.AIR);
	
	
	// ----- GOLD MAKING -----
	public static final CustomBlock CRACKED_GOLD_1 = new WoolBlock(DyeColor.YELLOW);
	public static final CustomBlock CRACKED_GOLD_2 = new WoolBlock(DyeColor.ORANGE);
	public static final CustomBlock CRACKED_GOLD_3 = new WoolBlock(DyeColor.MAGENTA);
	public static final CustomBlock REFINED_GOLD = new SimpleBlock(Material.GOLD_BLOCK);
	public static final ComparableBlock PISTON_BASE = new MaterialSet(Material.PISTON_EXTENSION, Material.PISTON_BASE);
	
	
	// ----- WALLS N STUFF -----
	
	
	public static boolean tryConvertBlock(Block block, ComparableBlock from, SettableBlock to) {
		boolean matches = from.matchesBlock(block);
		if (!matches) return false;
		
		to.setAtBlock(block);
		return true;
	}
}
