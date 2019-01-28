package deimophobe.nightfall.blocks;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.blocks.blocktype.BlockPlacer;
import deimophobe.nightfall.map.GameMap;
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
				new Conversion(NFBlocks.ENCHANTED_WALL, 4.5, 0.7, NFBlocks.NORMAL_WALL),
				new Conversion(NFBlocks.NORMAL_WALL, 4.0, 0.8, NFBlocks.CRACKED_WALL),
				new Conversion(NFBlocks.CRACKED_WALL, 4.0, 0.8, NFBlocks.DAMAGED_WALL),
				new Conversion(NFBlocks.DAMAGED_WALL, 3.5, 1.5, NFBlocks.BROKEN_WALL),
				new Conversion(NFBlocks.BROKEN_WALL, 3.0, 1.5, NFBlocks.AIR),
				new Conversion(NFBlocks.WOOL, 2.5, 1.5, NFBlocks.AIR),
				new Conversion(NFBlocks.CORRODED_WALL, 2.5, 1.5, NFBlocks.AIR),
				
				new Conversion(NFBlocks.NORMAL_STAIR, 4.5, 0.8, NFBlocks.DAMAGED_STAIR),
				new Conversion(NFBlocks.DAMAGED_STAIR, 4.0, 1.8, NFBlocks.AIR),
				
				new Conversion(NFBlocks.REINFORCED_SLAB, 4.0, 0.7, NFBlocks.NORMAL_SLAB),
				new Conversion(NFBlocks.NORMAL_SLAB, 4.0, 1.8, NFBlocks.DAMAGED_SLAB),
				new Conversion(NFBlocks.DAMAGED_SLAB, 3.5, 1.8, NFBlocks.AIR),
				
				new Conversion(NFBlocks.EXPLODEABLE_LIGHT, 2.0, 1.0, NFBlocks.AIR),
				new Conversion(NFBlocks.DECORATIVES, 1.0, 0.0, NFBlocks.AIR),
				new Conversion(NFBlocks.GRASS_BLOCK, 11.0, 5.0, NFBlocks.DIRT),
				new Conversion(NFBlocks.GRASS_BLOCK, 21.0, 15.0, NFBlocks.PODZOL)
		),
		CORROSION(
				new Conversion(NFBlocks.WALL, 2.2, 0.3, NFBlocks.CORRODED_WALL),
				new Conversion(NFBlocks.ALL_STAIRS, 2.2, 0.3, NFBlocks.CORRODED_WALL),
				new Conversion(NFBlocks.ALL_SLABS, 2.2, 0.3, NFBlocks.CORRODED_WALL),
				new Conversion(NFBlocks.CORRODED_WALL, 2.5, 0.3, NFBlocks.AIR),
				new Conversion(NFBlocks.EXPLODEABLE_LIGHT, 1.5, 1.0, NFBlocks.AIR),
				new Conversion(NFBlocks.DECORATIVES, 0.5, 0.0, NFBlocks.AIR)
		),
		THROWNEXPLOSION(
				new Conversion(NFBlocks.ENCHANTED_WALL, 4.5, 0.5, NFBlocks.NORMAL_WALL),
				new Conversion(NFBlocks.NORMAL_WALL, 4.5, 0.8, NFBlocks.CRACKED_WALL),
				new Conversion(NFBlocks.CRACKED_WALL, 4.5, 0.8, NFBlocks.DAMAGED_WALL),
				new Conversion(NFBlocks.WOOL, 2.5, 1.5, NFBlocks.AIR),
				new Conversion(NFBlocks.CORRODED_WALL, 2.5, 1.5, NFBlocks.AIR),
				
				new Conversion(NFBlocks.DECORATIVES, 1.0, 0.0, NFBlocks.AIR),
				new Conversion(NFBlocks.GRASS_BLOCK, 11.0, 5.0, NFBlocks.DIRT),
				new Conversion(NFBlocks.GRASS_BLOCK, 21.0, 15.0, NFBlocks.PODZOL)
		),
		MINOTAUR_CHARGE(
				new Conversion(NFBlocks.ENCHANTED_WALL, 2.3, 0.5, NFBlocks.NORMAL_WALL),
				new Conversion(NFBlocks.NORMAL_WALL, 1.5, 0.3, NFBlocks.CRACKED_WALL),
				new Conversion(NFBlocks.CRACKED_WALL, 1, 0.3, NFBlocks.DAMAGED_WALL),
				new Conversion(NFBlocks.DAMAGED_WALL, 1, 0.2, NFBlocks.BROKEN_WALL),
				new Conversion(NFBlocks.BROKEN_WALL, 0.5, 0.1, NFBlocks.AIR),
				new Conversion(NFBlocks.WOOL, 0.5, 0.1, NFBlocks.AIR),
				new Conversion(NFBlocks.EXPLODEABLE_LIGHT, 0.5, 1.0, NFBlocks.AIR),
				new Conversion(NFBlocks.DECORATIVES, 0.3, 0.3, NFBlocks.AIR)
		)
		
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
			
			GameMap map = GameMap.getCurrentMap();
			for (int x = startX; x<startX+size; x++) {
				for (int y = startY; y<startY+size; y++) {
					for (int z = startZ; z<startZ+size; z++) {
						Block block = world.getBlockAt(x, y, z);
						if (block.getType() == Material.AIR) continue;
						if (!map.isBlockBreakable(block)) continue;
						
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
		private final BlockMatcher from;
		private final double strength;
		private final double variation;
		private final BlockPlacer to;
		
		private Conversion(BlockMatcher from, double strength, double variation, BlockPlacer to) {
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
				NightfallPlugin.logger().warning("Tried to convert block that could not convert!? " +
						"BlockType: " + (block == null ? "block is null" : block.getType()) + " From: " + from);
			}
			
			double randStr = new Random().nextGaussian()*variation + strength;
			
			if (force > randStr) {
				to.setAtBlock(block);
				
				// This is so bad may the coding gods have mercy on us
				Material finalType = block.getType();
				switch (finalType) {
					case AIR:
					case CAVE_AIR:
					case VOID_AIR:{
						BlockManager manager = BlockManager.getManager();
						manager.checkTorchBreaking(block);
						break;
					}
				}
			}
			
			if (strength == 0)
				return 0;
			
			return force - strength;
		}
	}
	
	public static void convert(Type type, Location loc, double force) {
		type.convert(loc, force);
	}
	
	
	// MORTAR
	public static void mortar(Block center, int range, double successChance, boolean forceBlue) {
		World world = center.getWorld();
		
		int startX = center.getX() - range;
		int startY = center.getY() - range;
		int startZ = center.getZ() - range;
		
		int size = range * 2 + 1;
		
		// This is realllyyy ugly - and just bad
		for (int x = startX; x < startX + size; x++) {
			for (int y = startY; y < startY + size; y++) {
				for (int z = startZ; z < startZ + size; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (forceBlue) {
						NFBlocks.tryConvertBlock(block, NFBlocks.MORTARABLE_WALL, NFBlocks.ENCHANTED_WALL);
						NFBlocks.tryConvertBlock(block, NFBlocks.ALL_SLABS, NFBlocks.REINFORCED_SLAB);
						NFBlocks.tryConvertBlock(block, NFBlocks.ALL_STAIRS, NFBlocks.NORMAL_STAIR);
					} else {
						double random = Math.random();
						if (random <= successChance) {
							if (Math.random() <= 0.05) {
								if (NFBlocks.tryConvertBlock(block, NFBlocks.NORMAL_WALL, NFBlocks.ENCHANTED_WALL)) continue;
							}
							if (NFBlocks.tryConvertBlock(block, NFBlocks.CRACKED_WALL, NFBlocks.NORMAL_WALL)) continue;
							if (NFBlocks.tryConvertBlock(block, NFBlocks.DAMAGED_WALL, NFBlocks.CRACKED_WALL)) continue;
							
							if (NFBlocks.tryConvertBlock(block, NFBlocks.DAMAGED_STAIR, NFBlocks.NORMAL_STAIR)) continue;
							
							if (NFBlocks.tryConvertBlock(block, NFBlocks.NORMAL_SLAB, NFBlocks.REINFORCED_SLAB)) continue;
							if (NFBlocks.tryConvertBlock(block, NFBlocks.DAMAGED_SLAB, NFBlocks.NORMAL_SLAB)) continue;
						}
					}
				}
			}
		}
	}
}
