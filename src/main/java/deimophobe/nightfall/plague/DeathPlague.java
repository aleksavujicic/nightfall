package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * Created by Deimophobe on 10/03/17.
 */
class DeathPlague extends AbstractPlague {
	private Enderman death;
	private boolean killedOne = false;
	
	@Override
	public void startPlague(Set<Dwarf> plagueables, Set<Dwarf> plagued, int killAmt) {
		super.startPlague(plagueables, plagued, killAmt);
		
		World world = GameMap.getCurrentMap().getWorld();
		
		Location spawnLoc = GameMap.getCurrentMap().getDwarfSpawn().clone();
		spawnLoc.setY(0);
		
		death = createDeath(spawnLoc);
		world.playSound(GameMap.getCurrentMap().getDwarfSpawn(), Sound.ENTITY_ENDERMEN_STARE, 100, 1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				Dwarf target = getNextTarget();
				
				if (getAmountToKill() == 0 || target == null) {
					this.cancel();
					death.remove();
					notifyEnd();
					return;
				}
					
				death.teleport(target.getLocation());
				target.doDamage(null, GameDamageType.DEATH_PLAGUE, 10000, true, true);
				world.playSound(target.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
				world.playSound(target.getLocation(), "entity.endermen.scream", 1, 1);
				removeDwarf(target);
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 160, 40);
	}
	
	private Dwarf getNextTarget() {
		if (!plagued.isEmpty()) {
			return Misc.getRandom(plagued);
		}
		
		if (!killedOne) {
			killedOne = true;
			return getRandomPlagueable();
		} else {
			return getNearestPlagueable();
		}
	}
	
	private Dwarf getRandomPlagueable() {
		return Misc.getRandom(plagueables);
	}
	
	private Dwarf getNearestPlagueable() {
		Location deathLoc = death.getLocation();
		
		Dwarf nearestDwarf = null;
		double nearestDistance = Double.MAX_VALUE;

		for (Dwarf dwarf : plagueables) {
			double distance = deathLoc.distance(dwarf.getLocation());
			if (distance < nearestDistance) {
				nearestDwarf = dwarf;
				nearestDistance = distance;
			}
		}
		
		return nearestDwarf;
	}
	
	private static Enderman createDeath(Location spawnLoc) {
		Enderman enderman = (Enderman) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);
		enderman.setMetadata("death", new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
		enderman.setAI(false);
		enderman.setInvulnerable(true);
		enderman.setCollidable(false);
		enderman.setSilent(true);
		enderman.setRemoveWhenFarAway(false);
		return enderman;
	}
}
