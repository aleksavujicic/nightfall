package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.entity.Enderman;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class TwinsPlague extends Plague {
	
	private Dwarf target = null;
	private Enderman twin1;
	private Enderman twin2;
	
	private BukkitRunnable runner;
	
	@Override
	public void startPlague() {
		regenerateTwinsName();
		
		World world = GameMap.getCurrentMap().getWorld();
		
		Location spawnLoc = GameMap.getCurrentMap().getDwarfSpawn().clone();
		spawnLoc.setY(0);
		
		if (getAmountToKill(false) == 0) {
			target = getRandomPlagued();
		} else {
			target = getRandomPlagueable();
		}
		
		twin1 = createTwin(spawnLoc);
		twin2 = createTwin(spawnLoc);
		world.playSound(GameMap.getCurrentMap().getDwarfSpawn(), Sound.ENTITY_ENDERMEN_STARE, 100, 1);
		
		runner = new BukkitRunnable() {
			@Override
			public void run() {
				if (getAmountToKill(true) == 0) {
					endPlague();
				} else {
					target = getNextTarget();
					teleportTwins();
				}
			}
		};
		runner.runTaskTimer(NightfallPlugin.getPlugin(), 160, 40);
	}
	
	@Override
	public void endPlague() {
		super.endPlague();
		
		target = null;
		twin1.remove();
		twin2.remove();
		runner.cancel();
	}
	
	private Dwarf getNextTarget() {
		if (getAmountToKill(false) > 0) {
			return getNearestPlagueable();
		} else {
			return getRandomPlagued();
		}
	}
	
	private Dwarf getNearestPlagueable() {
		Location prevLoc = target.getLocation();
		
		Dwarf nearestDwarf = null;
		double nearestDistance = Double.MAX_VALUE;

		for (Dwarf dwarf : getPlagueables()) {
			double distance = prevLoc.distance(dwarf.getLocation());
			if (distance < nearestDistance) {
				nearestDwarf = dwarf;
				nearestDistance = distance;
			}
		}
		
		return nearestDwarf;
	}
	
	private void teleportTwins() {
		Location center = target.getLocation();
		Vector offset = center.getDirection();
		offset.setY(0);
		offset.normalize();
		
		Location twin1Loc = center.clone().add(offset);
		Location twin2Loc = center.clone().subtract(offset);
		Vector twin1Facing = offset.clone().multiply(-1);
		Vector twin2Facing = offset.clone();
		twin1Facing.setY(-1).normalize();
		twin2Facing.setY(-1).normalize();
		twin1Loc.setDirection(twin1Facing);
		twin2Loc.setDirection(twin2Facing);
		
		twin1.teleport(twin1Loc);
		twin2.teleport(twin2Loc);
		
		target.instaKill(null, GameDamageType.DEATH_PLAGUE);
		World world = GameMap.getCurrentMap().getWorld();
		world.playSound(center, Sound.ENTITY_ENDERMEN_TELEPORT, 1.5f, 1.2f);
		world.playSound(center, Sound.ENTITY_ENDERMEN_TELEPORT, 1.5f, 0.8f);
		world.playSound(center, "entity.endermen.scream", 1.5f, 1.2f);
		world.playSound(center, "entity.endermen.scream", 1.5f, 0.8f);
		world.playSound(center, "entity.endermen.ambient", 1.5f, 0.5f);
		
		Location bodyCenter = center.clone().add(0,0.5,0);
		world.spawnParticle(Particle.PORTAL, bodyCenter, 500, 0.5, 0.5, 0.5, 1.5);
		world.spawnParticle(Particle.SMOKE_LARGE, bodyCenter, 50, 0.5, 0.5, 0.5, 0.15);
		world.spawnParticle(Particle.FALLING_DUST, bodyCenter, 50, 0.5, 0.5, 0.5, 0);
		world.spawnParticle(Particle.CRIT_MAGIC, bodyCenter, 50, 0.5, 0.5, 0.8, 0);
	}
	
	private static Enderman createTwin(Location spawnLoc) {
		return spawnLoc.getWorld().spawn(spawnLoc, Enderman.class, enderman -> {
			enderman.setMetadata("death", new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
			enderman.setAI(false);
			enderman.setInvulnerable(true);
			enderman.setCollidable(false);
			enderman.setSilent(true);
			enderman.setRemoveWhenFarAway(false);
			enderman.setCarriedMaterial(new MaterialData(Material.AIR));
		});
	}
	
	
	private static String TWINS_NAME;
	
	public static String getTwinsName() {
		return TWINS_NAME;
	}
	
	private static void regenerateTwinsName() {
		String colour = ChatColor.DARK_GRAY.toString();
		String magic = ChatColor.MAGIC.toString();
		
		String plainName = "The Twins";
		StringBuilder modifiedName = new StringBuilder(colour);
		
		double chance = 0.5;
		
		for (char letter : plainName.toCharArray()) {
			if (letter == ' ') {
				modifiedName.append(letter);
				continue;
			}
			
			if (Math.random() < chance) {
				modifiedName.append(magic).append(letter).append(colour);
				chance -= 0.1;
			} else {
				modifiedName.append(letter);
				chance += 0.1;
			}
		}
		TWINS_NAME = modifiedName.toString();
	}
	
}
