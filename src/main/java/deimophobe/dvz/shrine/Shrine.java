package deimophobe.dvz.shrine;

import deimophobe.dvz.MapManager;
import deimophobe.dvz.Misc;
import deimophobe.dvz.shrine.region.CenteredRegion;
import deimophobe.dvz.shrine.region.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 21/01/17.
 */
public class Shrine {
	private final String name;
	private final Location mobSpawn;
	
	private final Region mobProtection;
	private final Region shrineProtection;
	private final CenteredRegion shrineRegion;
	
	private int shrinePower;
	private final int maxShrinePower;
	
	private final double goldWeight;
	private final int shrineNum;

	public String getName() {
		return name;
	}
	public Location getMobSpawn() {
		return mobSpawn;
	}
	
	public Region getMobProtection() {
		return mobProtection;
	}
	public Region getShrineProtection() {
		return shrineProtection;
	}
	public Region getShrineRegion() {
		return shrineRegion;
	}
	
	public double getGoldWeight() { return goldWeight; }
	
	public Shrine(String name, Location mobSpawn, Region mobProtection, Region shrineProtection, CenteredRegion shrineRegion, int maxShrinePower, double goldWeight, int shrineNum) {
		this.name = name;
		this.mobSpawn = mobSpawn;
		this.mobProtection = mobProtection;
		this.shrineProtection = shrineProtection;
		this.shrineRegion = shrineRegion;
		
		this.shrinePower = maxShrinePower;
		this.maxShrinePower = maxShrinePower;
		this.goldWeight = goldWeight;
		this.shrineNum = shrineNum;
	}
	
	public static Shrine createShrine(ConfigurationSection section, int shrineNum) {
		
		String name = section.getString("name");
		Location mobSpawn = Misc.createLocation(section.getDoubleList("mobspawn"));
		
		Region mobProt = Region.createRegion(section.getConfigurationSection("mobprot"));
		Region shrineProt = Region.createRegion(section.getConfigurationSection("shrineprot"));
		CenteredRegion shrine = CenteredRegion.createRegion(section.getConfigurationSection("shrine"));
		
		int maxShrinePower = section.getInt("power");
		double goldWeight = section.getDouble("goldweight");

		return new Shrine(name, mobSpawn, mobProt, shrineProt, shrine, maxShrinePower, goldWeight, shrineNum);
	}
	
	public boolean damageShrine(int mobNum, int dwarfNum) {
		int damage = 0;
		int recovery = 0;
		// Making shrines a bit stronger
		if (shrineNum == 0) {
			dwarfNum = 0;
		} else if ((shrineNum + 1) == ShrineManager.getManager().getNumShrines()) {
			dwarfNum = 2;

			// Final shrine should not fall until most dwarves are dead
			dwarfNum *= 3;
		} else {
			dwarfNum = 2;
		}
		if (mobNum == 0) {
			// Regen when no mobs around, first shrine has slower regen
			if (shrineNum == 0 && shrinePower < (maxShrinePower / 4)) {
				recovery = dwarfNum * (maxShrinePower / 100);
			}
			else if (shrineNum != 0){
				recovery = dwarfNum * (maxShrinePower / 40);
			}
		}
		// 1:1 dwarf:zombie, but as long as there's a mob on shrine it will lose power slowly
		// First two zombies do half shrine damage
		else {
			damage += Math.min(2, mobNum) * (maxShrinePower / 50) +  Math.max(0, (mobNum - 2)) * (maxShrinePower / 25);
			damage -= Math.min(2, dwarfNum) * (maxShrinePower / 50) + Math.max(0, (dwarfNum - 2)) * (maxShrinePower / 25);
			if (damage < (maxShrinePower / 100)) damage = (maxShrinePower / 100);
		}
		// 3 times or above as much dwarves will prevent shrine from losing power
		if (damage > 0 && (dwarfNum >= (mobNum * 3))) {
			damage = 0;
		}
		// At 500 gold shrine regen drops off linearly until at 100 gold to 20% regen speed, also recovery is always nonnegative
		if (recovery > 0) {
			recovery = recovery * Math.max(100, Math.min(500, ShrineManager.getManager().getGold())) / 500;
		}
		else
		{
			recovery = 0;
		}
		// Shrine damage and recovery are capped at 20%
		damage = Math.min((maxShrinePower / 5), damage);
		recovery = Math.min((maxShrinePower / 5), recovery);
		shrinePower -= damage;
		shrinePower += recovery;

		// Shrine Power capped at max shrine power
		if (shrinePower > maxShrinePower){
			shrinePower = maxShrinePower;
		}
		return (shrinePower <= 0);
	}

	public boolean damageShrine(int damage) {
		shrinePower -= damage * maxShrinePower / 100;
		if (shrinePower > maxShrinePower){
			shrinePower = maxShrinePower;
		}
		return (shrinePower <= 0);
	}

	public float getFractionalShrinePower() {
		return (float) shrinePower/maxShrinePower;
	}
	
	public Location getLocation() {
		return shrineRegion.getCenter();
	}
	
	void explodeShrine() {
		Location center = shrineRegion.getCenter();
		World world = center.getWorld();
		
		world.spawnParticle(Particle.EXPLOSION_LARGE, center, 4, 5, 2, 5);
		world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 3f, 0.6f);
		world.playSound(center, "horn", 100f, 1f);
		
		if (!MapManager.getManager().isEnabled()) return;
		
		Set<Block> blocks = new HashSet<>();
		
		int radius = 3;
		
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Block block = center.clone().add(x,y,z).getBlock();
					
					if (block.getType() == Material.ENDER_PORTAL_FRAME) {
						blocks.add(block);
						continue;
					}
					
					if (block.getType() == Material.BEACON) {
						block.setType(Material.AIR);
						continue;
					}
					
					if (block.getType() == Material.BEDROCK) {
						continue;
					}
					
					if (Math.random() <= 0.65) {
						if (Math.random() <= 0.4) {
							blocks.add(block);
						} else {
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
		
		for (Block block : blocks) {
			FallingBlock falling = world.spawnFallingBlock(block.getLocation(), block.getState().getData());
			falling.setDropItem(false);
			
			double vx = 1*Math.random() - 0.5;
			double vy = 1*Math.random() + 1;
			double vz = 1*Math.random() - 0.5;
			
			falling.setVelocity(new Vector(vx, vy, vz));
			
			block.setType(Material.AIR);
		}
	}
}
