package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class TwinsPlague extends Plague {
	private static final Particle.DustOptions BLACK_DUST = new Particle.DustOptions(Color.BLACK, 1.2f);
	
	public static void killMoreDwarves(int num, boolean enraged) {
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
		}, enraged);
	}
	
	
	@Override
	public void startPlague() {
		boolean enraged = (Math.random() < 0.01) && (getAmountToKill(true) >= 4);
		new TwinsRampage(new PlagueDwarfChooser(), enraged);
	}
	
	
	private static class TwinsRampage {
		private final Supplier<Dwarf> dwarfChooser;
		private final Enderman twin1;
		private final Enderman twin2;
		
		private final BukkitRunnable runner;
		
		private final boolean enraged;
		private final Set<Enderman> fakeTwins = new HashSet<>();
		
		private boolean running = true;
		
		private TwinsRampage(Supplier<Dwarf> dwarfChooser, boolean enraged) {
			this.enraged = enraged;
			regenerateTwinsName(enraged);
			
			this.dwarfChooser = dwarfChooser;
			
			// Warning: setting spawnLoc with y < 0 or far away cause the twins to be invisible.
			Location spawnLoc = GameMap.getCurrentMap().getDwarfSpawn().clone();
			spawnLoc.setY(0);
			this.twin1 = createTwin(spawnLoc);
			this.twin2 = createTwin(spawnLoc);
			
			World world = spawnLoc.getWorld();
			world.playSound(spawnLoc, Sound.ENTITY_ENDERMAN_STARE, 100, 1);
			if (enraged) {
				world.playSound(spawnLoc, Sound.ENTITY_ENDERMAN_STARE, 100, 0.8f);
				//world.playSound(spawnLoc, Sound.ENTITY_ENDERMEN_STARE, 100, 0.5f);
			}
			
			
			int delay = (enraged ? 300 : 160);
			int period = (enraged ? 10 : 40);
			this.runner = new BukkitRunnable() {
				@Override
				public void run() {
					teleportTwins();
				}
			};
			runner.runTaskTimer(NightfallPlugin.getPlugin(), delay, period);
			
			
			if (enraged) {
				new BukkitRunnable() {
					@Override
					public void run() {
						final int dwarfEndermen = DwarfManager.getManager().getNumberOfPlayers()/4 + 1;
						
						for (int i=0; i < dwarfEndermen; i++) {
							Dwarf dwarf = Misc.getRandom(DwarfManager.getManager().getDwarves());
							if (!running || dwarf == null) {
								this.cancel();
								return;
							}
							
							Location fakeSpawn = Misc.randomLocation(dwarf.getLocation(), 10, 5, 10);
							Vector facing = dwarf.getLocation().subtract(fakeSpawn).toVector();
							fakeSpawn.setDirection(facing);
							
							Enderman fakeTwin = createTwin(fakeSpawn);
							fakeTwin.teleport(fakeSpawn);
							fakeTwins.add(fakeTwin);
						}
						
						for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
							Location bodyCenter = dwarf.getEyeLocation();
							Player player = dwarf.getPlayer();
							player.spawnParticle(Particle.PORTAL, bodyCenter, 50, 5, 5, 5, 1.5);
							player.spawnParticle(Particle.SMOKE_LARGE, bodyCenter, 10, 5, 5, 5, 0.15);
							player.spawnParticle(Particle.REDSTONE, bodyCenter, 10, 5, 5, 5, BLACK_DUST);
							player.spawnParticle(Particle.CRIT_MAGIC, bodyCenter, 10, 5, 5, 5, 0);
						}
					}
				}.runTaskTimer(NightfallPlugin.getPlugin(), 20, 5);
			}
		}
		
		private void teleportTwins() {
			if (enraged) regenerateTwinsName(enraged);
			Dwarf target = dwarfChooser.get();
			
			if (target == null) {
				running = false;
				
				twin1.remove();
				twin2.remove();
				runner.cancel();
				
				fakeTwins.forEach(Entity::remove);
				fakeTwins.clear();
				
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
			float volume = (enraged ? 100f : 1.5f);
			world.playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, volume, 1.2f);
			world.playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, volume, 0.8f);
			world.playSound(center, Sound.ENTITY_ENDERMAN_SCREAM, volume, 1.2f);
			world.playSound(center, Sound.ENTITY_ENDERMAN_SCREAM, volume, 0.8f);
			world.playSound(center, Sound.ENTITY_ENDERMAN_AMBIENT, volume, 0.5f);
			if (enraged) {
				float pitch = Misc.randomFloat(0.5f, 1f);
				world.playSound(center, Sound.ENTITY_ENDERMAN_DEATH, 100, pitch);
				
				target.givePotionEffect(PotionEffectType.BLINDNESS, 200, 2, false, false, true);
			}
			
			Location bodyCenter = center.clone().add(0,0.5,0);
			world.spawnParticle(Particle.PORTAL, bodyCenter, 200, 0.5, 0.5, 0.5, 1.5);
			world.spawnParticle(Particle.SMOKE_LARGE, bodyCenter, 50, 0.5, 0.5, 0.5, 0.15);
			world.spawnParticle(Particle.REDSTONE, bodyCenter, 50, 0.5, 0.5, 0.5, BLACK_DUST);
			world.spawnParticle(Particle.CRIT_MAGIC, bodyCenter, 50, 0.5, 0.5, 0.5, 0);
			world.spawnParticle(Particle.ENCHANTMENT_TABLE, bodyCenter, 150, 0.75, 1, 0.75, 0.1);
		}
		
		private Enderman createTwin(Location spawnLoc) {
			
			return spawnLoc.getWorld().spawn(spawnLoc, Enderman.class, enderman -> {
				enderman.setAI(false);
				enderman.setInvulnerable(true);
				enderman.setCollidable(false);
				enderman.setSilent(true);
				enderman.setRemoveWhenFarAway(false);
				enderman.setCarriedMaterial(new MaterialData(Material.AIR));
				
				if (enraged) enderman.setTarget(enderman);
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
	
	private static void regenerateTwinsName(boolean enraged) {
		String plainName = "The Twins";
		
		Supplier<String> colour, magic;
		if (enraged) {
			colour = () -> {
				if (Math.random() < 0.5) return ChatColor.RED.toString();
				else return ChatColor.DARK_RED.toString();
			};
			
			magic = () -> {
				double rand = Math.random()*5;
				if (rand < 1) return ChatColor.BOLD.toString();
				else if (rand < 2) return ChatColor.UNDERLINE.toString();
				else if (rand < 3) return ChatColor.STRIKETHROUGH.toString();
				else if (rand < 4) return ChatColor.ITALIC.toString() + ChatColor.BOLD;
				else return ChatColor.MAGIC.toString();
			};
			
			plainName = plainName.toUpperCase();
		} else {
			colour = () -> ChatColor.DARK_GRAY.toString();
			magic = () -> ChatColor.MAGIC.toString();
		}
		
		StringBuilder modifiedName = new StringBuilder(colour.get());
		double chance = 0.5;
		
		for (char letter : plainName.toCharArray()) {
			if (letter == ' ') {
				modifiedName.append(letter);
				continue;
			}
			
			if (Math.random() < chance || enraged) {
				modifiedName.append(magic.get()).append(letter).append(colour.get());
				chance -= 0.1;
			} else {
				modifiedName.append(letter);
				chance += 0.1;
			}
		}
		TWINS_NAME = modifiedName.toString();
	}
	
}
