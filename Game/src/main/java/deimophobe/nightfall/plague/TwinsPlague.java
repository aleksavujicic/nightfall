package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.entity.Enderman;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class TwinsPlague extends Plague {
	
	public static void killMoreDwarves(int num) {
		new TwinsRampage(new Supplier<Dwarf>() {
			int toKill = num;
			
			@Override
			public Dwarf get() {
				if (toKill > 0) {
					toKill--;
					return Plague.getRandomPlagueable();
				} else {
					return null;
				}
			}
		});
	}
	
	
	@Override
	public void startPlague() {
		new TwinsRampage(new PlagueDwarfChooser());
	}
	
	
	private static class TwinsRampage {
		private final Supplier<Dwarf> dwarfChooser;
		private final Enderman twin1;
		private final Enderman twin2;
		
		private final BukkitRunnable runner;
		
		private TwinsRampage(Supplier<Dwarf> dwarfChooser) {
			regenerateTwinsName();
			this.dwarfChooser = dwarfChooser;
			
			twin1 = createTwin();
			twin2 = createTwin();
			
			Location spawnLoc = GameMap.getCurrentMap().getDwarfSpawn().clone();
			spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_ENDERMEN_STARE, 100, 1);
			
			runner = new BukkitRunnable() {
				@Override
				public void run() {
					teleportTwins();
				}
			};
			runner.runTaskTimer(NightfallPlugin.getPlugin(), 160, 40);
		}
		
		private void teleportTwins() {
			Dwarf target = dwarfChooser.get();
			
			if (target == null) {
				twin1.remove();
				twin2.remove();
				runner.cancel();
				
				return;
			}
			
			Location center = target.getLocation();
			Vector offset = center.getDirection();
			offset.setY(0);
			offset.normalize();
			Misc.rotateVector(offset, Math.PI/2);
			
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
		
		private static Enderman createTwin() {
			// Warning: setting spawnLoc with y < 0 or far away cause the twins to be invisible.
			Location spawnLoc = GameMap.getCurrentMap().getDwarfSpawn().clone();
			spawnLoc.setY(0);
			
			return spawnLoc.getWorld().spawn(spawnLoc, Enderman.class, enderman -> {
				enderman.setAI(false);
				enderman.setInvulnerable(true);
				enderman.setCollidable(false);
				enderman.setSilent(true);
				enderman.setRemoveWhenFarAway(false);
				enderman.setCarriedMaterial(new MaterialData(Material.AIR));
			});
		}
	}
	
	// ----- DWARF SUPPLIER -----
	
	private class PlagueDwarfChooser implements Supplier<Dwarf> {
		private Location lastLocation = null;
		
		private PlagueDwarfChooser() {
			// Start at any random dwarf (note that while this may pick immune dwarves, the get() method will not).
			Dwarf randDwarf = Misc.getRandom(DwarfManager.getManager().getDwarves());
			if (randDwarf != null) lastLocation = randDwarf.getLocation();
		}
		
		@Override
		public Dwarf get() {
			if (lastLocation == null) return null;
			
			if (getAmountToKill(true) == 0) {
				// No more needed to kill we're done.
				endPlague();
				return null;
			} else if (getAmountToKill(false) > 0) {
				// Still need to kill non-plagued ppl.
				return getNearestPlagueable();
			} else {
				// Only left to kill plagued ppl.
				return getRandomPlagued();
			}
		}
		
		private Dwarf getNearestPlagueable() {
			Dwarf nearestDwarf = null;
			double nearestDistance = Double.MAX_VALUE;
			
			for (Dwarf dwarf : getPlagueables()) {
				double distance = lastLocation.distance(dwarf.getLocation());
				if (distance < nearestDistance) {
					nearestDwarf = dwarf;
					nearestDistance = distance;
				}
			}
			
			return nearestDwarf;
		}
	}
	
	
	
	// ----- TWINS NAME IN CHAT -----
	
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
