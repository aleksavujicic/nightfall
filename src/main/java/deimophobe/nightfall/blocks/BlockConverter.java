package deimophobe.nightfall.blocks;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.blocks.blocktype.ComparableBlock;
import deimophobe.nightfall.blocks.blocktype.SettableBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
				
				new Conversion(BlockType.NORMAL_STAIR, 4.5, 0.8, BlockType.DAMAGED_STAIR),
				new Conversion(BlockType.DAMAGED_STAIR, 4.0, 1.8, BlockType.AIR),
				
				new Conversion(BlockType.REINFORCED_SLAB, 4.0, 0.7, BlockType.NORMAL_SLAB),
				new Conversion(BlockType.NORMAL_SLAB, 4.0, 1.8, BlockType.DAMAGED_SLAB),
				new Conversion(BlockType.DAMAGED_SLAB, 3.5, 1.8, BlockType.AIR),
				
				new Conversion(BlockType.LIGHT, 3.0, 1.5, BlockType.AIR)
		),
		CORROSION(
				new Conversion(BlockType.WALL, 4.0, 1.5, BlockType.CORRODED_WALL),
				new Conversion(BlockType.ALL_STAIRS, 4.0, 1.5, BlockType.CORRODED_WALL),
				new Conversion(BlockType.ALL_SLABS, 4.0, 1.5, BlockType.CORRODED_WALL),
				new Conversion(BlockType.CORRODED_WALL, 4.0, 1.5, BlockType.AIR),
				new Conversion(BlockType.LIGHT, 3.0, 1.5, BlockType.AIR)
		),
		THROWNEXPLOSION(
				new Conversion(BlockType.NORMAL_WALL, 4.5, 0.8, BlockType.CRACKED_WALL),
				new Conversion(BlockType.CRACKED_WALL, 4.5, 0.8, BlockType.DAMAGED_WALL),
				new Conversion(BlockType.ALL_WOOLS, 2.0, 1.0, BlockType.AIR)
		),
		MINOTAUR_CHARGE(
				new Conversion(BlockType.ENCHANTED_WALL, 2.3, 0.5, BlockType.NORMAL_WALL),
				new Conversion(BlockType.NORMAL_WALL, 1.5, 0.3, BlockType.CRACKED_WALL),
				new Conversion(BlockType.CRACKED_WALL, 1, 0.3, BlockType.DAMAGED_WALL),
				new Conversion(BlockType.DAMAGED_WALL, 1, 0.2, BlockType.BROKEN_WALL),
				new Conversion(BlockType.BROKEN_WALL, 0.5, 0.1, BlockType.AIR)
		),
		ARROW_DAMAGE,
		;
		
		
		private final List<Conversion> conversions = new ArrayList<>();
		
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
						Block block = world.getBlockAt(x, y, z);
						if (block.getType() == Material.AIR) continue;
						
						double appliedForce = (force / Math.max(1 , Math.sqrt(loc.distance(new Location(world, 0.5 + x, 0.5 + y, 0.5 + z)))));
						// Bukkit.broadcastMessage(""+appliedForce);
						while (appliedForce > 0) {
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
			
			return force - strength;
		}
	}
	
	public static void convert(Type type, Location loc, double force) {
		type.convert(loc, force);
	}
	
	
	private static final int MORTAR_RANGE = 4; // Half range
	private static final double MORTAR_CHANCE = 0.02; // Half range
	// MORTAR
	public static void mortar(Block center, boolean wizzy) {
		World world = center.getWorld();
		
		int startX = center.getX() - MORTAR_RANGE;
		int startY = center.getY() - MORTAR_RANGE;
		int startZ = center.getZ() - MORTAR_RANGE;
		
		int size = MORTAR_RANGE * 2 + 1;
		
		for (int x = startX; x < startX + size; x++) {
			for (int y = startY; y < startY + size; y++) {
				for (int z = startZ; z < startZ + size; z++) {
					Block block = world.getBlockAt(x, y, z);
					
					if (wizzy || Math.random() <= MORTAR_CHANCE)
						BlockType.tryConvertBlock(block, BlockType.MORTARABLE_WALL, BlockType.ENCHANTED_WALL);
					else
						BlockType.tryConvertBlock(block, BlockType.MORTARABLE_WALL, BlockType.NORMAL_WALL);
					
					BlockType.tryConvertBlock(block, BlockType.ALL_SLABS, BlockType.REINFORCED_SLAB);
					BlockType.tryConvertBlock(block, BlockType.ALL_STAIRS, BlockType.NORMAL_STAIR);
				}
			}
		}
	}
}
