package deimophobe.dvz.blocks;

import deimophobe.dvz.blocks.blocktype.BlockType;
import deimophobe.dvz.blocks.blocktype.ComparableBlock;
import deimophobe.dvz.blocks.blocktype.SettableBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

/**
 * Created by Deimophobe on 28/02/17.
 */
public class BlockConverter {
	public enum Type {
		EXPLOSION(
				new Conversion(BlockType.ENCHANTED_WALL, 4.5, 0.7, BlockType.NORMAL_WALL),
				new Conversion(BlockType.NORMAL_WALL, 4.0, 0.8, BlockType.CRACKED_WALL),
				new Conversion(BlockType.CRACKED_WALL, 4.0, 0.8, BlockType.DAMAGED_WALL),
				new Conversion(BlockType.DAMAGED_WALL, 3.5, 1.5, BlockType.BROKEN_WALL),
				new Conversion(BlockType.BROKEN_WALL, 3.0, 1.5, BlockType.AIR),
				new Conversion(BlockType.ALL_WOOLS, 2.0, 0.5, BlockType.AIR),
				
				new Conversion(BlockType.NORMAL_STAIR, 4.0, 0.8, BlockType.DAMAGED_STAIR),
				new Conversion(BlockType.DAMAGED_STAIR, 3.5, 1.2, BlockType.AIR),
				
				new Conversion(BlockType.REINFORCED_SLAB, 4.0, 0.7, BlockType.NORMAL_SLAB),
				new Conversion(BlockType.NORMAL_SLAB, 4.0, 1.2, BlockType.DAMAGED_SLAB),
				new Conversion(BlockType.DAMAGED_SLAB, 3.5, 1.2, BlockType.AIR),
				
				new Conversion(BlockType.LIGHT, 3.0, 1.5, BlockType.AIR)
		),
		CORROSION(
				new Conversion(BlockType.WALL, 4.0, 1.5, BlockType.CORRODED_WALL),
				new Conversion(BlockType.CORRODED_WALL, 4.0, 1.5, BlockType.AIR)
		),
		THROWNEXPLOSION(
				new Conversion(BlockType.NORMAL_WALL, 4.5, 0.8, BlockType.CRACKED_WALL),
				new Conversion(BlockType.CRACKED_WALL, 4.5, 0.8, BlockType.DAMAGED_WALL),
				new Conversion(BlockType.ALL_WOOLS, 2.0, 1.0, BlockType.AIR)
		),
		MORTAR(
				new Conversion(BlockType.UNENCHANTED_WALL, 0.0, 0.0, BlockType.NORMAL_WALL),
				new Conversion(BlockType.DAMAGED_STAIR, 0.0, 0.0, BlockType.NORMAL_STAIR),
				new Conversion(BlockType.DAMAGED_SLAB, 0.0, 0.0, BlockType.REINFORCED_SLAB),
				new Conversion(BlockType.NORMAL_SLAB, 0.0, 0.0, BlockType.REINFORCED_SLAB),
				new Conversion(BlockType.WALL, 10.0, 1.0, BlockType.ENCHANTED_WALL)
		),
		ARROW_DAMAGE,;
		
		
		private final Set<Conversion> conversions = new HashSet<>();
		
		Type(Conversion... conversions) {
			Collections.addAll(this.conversions, conversions);
		}
		
		
		private void convert(Location loc, double force) {
			Location bottomCorner = loc.clone().subtract(force, force, force);
			World world = loc.getWorld();
			int size = (int) Math.ceil(2*force);
			
			int startX = bottomCorner.getBlockX();
			int startY = bottomCorner.getBlockY();
			int startZ = bottomCorner.getBlockZ();
			
			for (int x = startX; x<startX+size; x++) {
				for (int y = startY; y<startY+size; y++) {
					for (int z = startZ; z<startZ+size; z++) {
						double appliedForce = (force / Math.max(1 , Math.sqrt(loc.distance(new Location(world, 0.5 + x, 0.5 + y, 0.5 + z)))));
						// Bukkit.broadcastMessage(""+appliedForce);
						while (appliedForce > 0) {
							Block block = world.getBlockAt(x, y, z);
							boolean didConvert = false;
							for (Conversion conv : conversions) {
								if (conv.canConvert(block)) {
									appliedForce = conv.tryConvert(block, appliedForce);
									didConvert = true;
								}
							}
							if (!didConvert)
								break;
						}
					}
				}
			}
			
		}
	}
	
	private static class Conversion {
		private final ComparableBlock from;
		private final double strength;
		private final double variation;
		private final SettableBlock to;
		
		private Conversion(ComparableBlock from, double strength, double variation, SettableBlock to) {
			this.from = from;
			this.strength = strength;
			this.variation = variation;
			this.to = to;
		}
		
		private boolean canConvert(Block block) {
			return from.matchesBlock(block);
		}
		
		private double tryConvert(Block block, double force) {
			if (!canConvert(block)) {
				Bukkit.getLogger().warning("Tried to convert block that could not convert!? " +
						"BlockType: " + (block == null ? "block is null" : block.getType()) + " From: " + from);
			}
			
			double randStr = new Random().nextGaussian()*variation + strength;
			
			if (force > randStr) {
				to.setAtBlock(block);
			}
			
			if (strength == 0)
				return 0;
			
			return force - randStr;
		}
	}
	
	public static void convert(Type type, Location loc, double force) {
		type.convert(loc, force);
	}
}
