package deimophobe.dvz.blocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

/**
 * Created by Deimophobe on 28/02/17.
 */
public class BlockConverter {
	public enum Type {
		EXPLOSION(
				new Conversion(Material.LAPIS_ORE, 2, 1, Material.SMOOTH_BRICK),
				new Conversion(Material.SMOOTH_BRICK, 1.5, 0.5, Material.COBBLESTONE),
				new Conversion(Material.COBBLESTONE, 0.75, 0.25, Material.GRAVEL),
				new Conversion(Material.GRAVEL, 0.25, 0.25, Material.AIR),
				new Conversion(Material.WOOL, 0.25, 0, Material.AIR)
		),
		CORROSION(
				// TODO only specific data values
				new Conversion(Material.LAPIS_ORE, 1.5, 0.5, Material.WOOL),
				new Conversion(Material.SMOOTH_BRICK, 1, 0.5, Material.WOOL),
				new Conversion(Material.COBBLESTONE, 1, 0.25, Material.WOOL),
				new Conversion(Material.GRAVEL, 0.5, 0.25, Material.AIR),
				new Conversion(Material.WOOL, 0.5, 0, Material.AIR)
		),
		MORTAR,
		ARROW_DAMAGE,;
		
		
		private final Map<Material, Conversion> conversions = new HashMap<>();
		Type(Conversion... conversions) {
			for (Conversion conversion : conversions) {
				this.conversions.put(conversion.getFrom(), conversion);
			}
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
						double appliedForce = force - loc.distance(new Location(world, 0.5 + x, 0.5 + y, 0.5 + z));
						//Bukkit.broadcastMessage(""+appliedForce);
						while (appliedForce > 0) {
							Block block = world.getBlockAt(x, y, z);
							Conversion conv = conversions.get(block.getType());
							if (conv == null) break;
							
							appliedForce = conv.tryConvert(block, appliedForce);
						}
					}
				}
			}
			
		}
	}
	
	private static class Conversion {
		private final Material from;
		private final double strength;
		private final double variation;
		private final Material to;
		
		private Material getFrom() {
			return from;
		}
		
		private Conversion(Material from, double strength, double variation, Material to) {
			this.from = from;
			this.strength = strength;
			this.variation = variation;
			this.to = to;
		}
		
		private boolean canConvert(Block block) {
			return (block != null && block.getType() == from);
		}
		
		private double tryConvert(Block block, double force) {
			if (!canConvert(block)) {
				Bukkit.getLogger().warning("Tried to convert block that could not convert!? " +
						"BlockType: " + (block == null ? "block is null" : block.getType()) + " From: " + from);
			}
			
			double randStr = new Random().nextGaussian()*variation + strength;
			
			if (force >= randStr) {
				block.setType(to);
			}
			return force - randStr*2;
		}
	}
	
	public static void convert(Type type, Location loc, double force) {
		type.convert(loc, force);
	}
}
