package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.event.GameStartEvent;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.util.LifetimeObject;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class ConsecratingCharm extends Consumable {
	private static final double TWO_PI = 2 * Math.PI;
	private static final ConsumeResult TOO_CLOSE = ConsumeResult.failedResultWithMessage("Too close to existing charm");
	
	private final Set<CharmInstance> activeCharms = new HashSet<>();
	
	ConsecratingCharm(String item) {
		super(item);
		NightfallPlugin.getPlugin().registerListener(new CharmResetter());
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!click.isLeftClick()) return ConsumeResult.FAILURE;
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		
		Location center = dwarf.getEyeLocation();
		boolean success = spawnCharm(center, 8*20, 11, 3);
		
		if (success) {
			return ConsumeResult.SUCCESS;
		} else {
			return TOO_CLOSE;
		}
	}
	
	private boolean isCloseToActiveCharm(Location center) {
		for (CharmInstance charm : activeCharms) {
			if (charm.center.distance(center) <= charm.radius) return true;
		}
		return false;
	}
	
	public boolean spawnCharm(Location center, int lifetime, double radius, int numSwords) {
		if (isCloseToActiveCharm(center)) return false;
		
		center.getWorld().playSound(center, "entity.evocation_illager.prepare_summon", 1f, 1f);
		CharmInstance charm = new CharmInstance(lifetime, center, radius, numSwords);
		activeCharms.add(charm);
		return true;
	}
	
	private class CharmInstance extends LifetimeObject {
		private final Location center;
		private final double radius;
		private final int maxLifetime;
		private final List<ArmorStand> swords = new ArrayList<>();
		private final Set<Location> particleLocations = new HashSet<>();
		
		protected CharmInstance(int lifetime, Location center, double radius, int numSwords) {
			super(lifetime, 1);
			
			this.center = center;
			this.radius = radius;
			this.maxLifetime = lifetime;
			
			// Set sword spawn locations
			List<Location> swordLocations = new ArrayList<>();
			double randomPhase = Misc.randomDouble(0, TWO_PI);
			for (int i=0; i<numSwords ; i++) {
				double r = radius + Misc.randomDouble(-2, 0);
				double theta = TWO_PI * i/numSwords + Misc.randomDouble(-0.3,0.3) + randomPhase;
				
				double x = r * Math.cos(theta);
				double z = r * Math.sin(theta);
				double y = 3;
				
				Location position = center.clone().add(x, y, z);
				swordLocations.add(position.add(0, 1, 0));
			}
			
			
			// Spawn swords and set particle locations
			for (int i=0; i<numSwords ; i++) {
				int prevIndex = (i + numSwords - 1) % numSwords; // The + NUM_SWORDS is to ensure the value is positive
				int nextIndex = (i + 1) % numSwords;
				
				// Get current, next and previous sword locations
				Location prevLocation = swordLocations.get(prevIndex);
				Location currLocation = swordLocations.get(i);
				Location nextLocation = swordLocations.get(nextIndex);
				
				// Calculate offset vectors and sizes
				Vector prevOffset = prevLocation.clone().subtract(currLocation).toVector();
				Vector nextOffset = nextLocation.clone().subtract(currLocation).toVector();
				double nextSize = nextOffset.length();
				prevOffset.normalize();
				nextOffset.normalize();
				
				// Set the facing direction of curr location to be halfway between previous and next location
				Vector facing = prevOffset.clone().add(nextOffset);
				currLocation.setDirection(facing);
				// Summon sword
				swords.add(summonSword(currLocation));
				
				// Set particle spawn locations
				Vector delta = nextOffset.clone();
				Location particleLoc = currLocation.clone().add(0,1,0);
				for (int j=0; j<nextSize; j++) {
					particleLocations.add(particleLoc.clone());
					particleLoc.add(delta);
				}
			}
			
			freezeMobs();
			spawnSwordParticles();
		}
		
		@Override
		public void run() {
			super.run();
			
			if (everyNthTick(2)) {
				spawnAmbientParticles();
			}
			
			if (everyNthTick(10)) {
				freezeMobs();
			}
			
			if (everyNthTick(20)) {
				spawnSwordParticles();
			}
		}
		
		private void spawnSwordParticles() {
			World world = center.getWorld();
			for (Location particleLoc : particleLocations) {
				world.spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
			}
		}
		
		private void spawnAmbientParticles() {
			center.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, center, 1, radius/2, 4, radius/2, 0.05);
			center.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, center, 10, radius/2, 4, radius/2, 0.05);
		}
		
		private void freezeMobs() {
			for (MonsterPlayer mp : MonsterManager.getManager().getAlivePlayerMobs()) {
				if (mp.isMobAlive() && center.distance(mp.getLocation()) <= radius) {
					int mobFreezeTime = getLifeLeft() + mp.getMob().getCharmTime() - maxLifetime;
					mp.freeze(mobFreezeTime);
				}
			}
			AIManager.getManager().clearArea(center, 1.5*radius);
		}
		
		@Override
		public synchronized void cancel() throws IllegalStateException {
			super.cancel();
			for (ArmorStand sword : swords) {
				sword.remove();
			}
			activeCharms.remove(this);
		}
		
		private ArmorStand summonSword(Location loc) {
			return loc.getWorld().spawn(loc, ArmorStand.class, (stand) -> {
				stand.setHelmet(getItemStack());
				stand.setHeadPose(new EulerAngle(Math.PI, 0, 0));
				stand.setVisible(false);
				stand.setBasePlate(false);
				stand.setGravity(false);
			});
		}
	}
	
	private class CharmResetter implements Listener {
		@EventHandler
		public void onGameStart(GameStartEvent event) {
			for (CharmInstance charm : new HashSet<>(activeCharms)) {
				charm.cancel();
			}
			activeCharms.clear();
		}
	}
}
