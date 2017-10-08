package deimophobe.nightfall.plague;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
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
		
		death = (Enderman) world.spawnEntity(spawnLoc, EntityType.ENDERMAN);
		death.setMetadata("death", new FixedMetadataValue(NightfallPlugin.getPlugin(), true));
		death.setAI(false);
		death.setInvulnerable(true);
		death.setCollidable(false);
		death.setSilent(true);
		death.setRemoveWhenFarAway(false);
		world.playSound(GameMap.getCurrentMap().getDwarfSpawn(), Sound.ENTITY_ENDERMEN_STARE, 100, 1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (getAmountToKill() > 0) {
					Dwarf target;
					if (killedOne) {
						target = getNearestPlagueable();
					} else {
						target = getRandomPlagueable();
					}
					
					if (target == null) {
						this.cancel();
						death.remove();
						notifyEnd();
						return;
					}
					
					killedOne = true;
					death.teleport(target.getLocation());
					
					target.doDamage(null, CustomDamageType.DEATH_PLAGUE, 10000, true, true);
					
					
					world.playSound(target.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
					world.playSound(target.getLocation(), "entity.endermen.scream", 1, 1);
					removeDwarf(target);
				} else {
					this.cancel();
					death.remove();
					notifyEnd();
				}
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 160, 40);
	}
	
	private Dwarf getRandomPlagueable() {
		return Misc.getRandom(plagueables);
	}
	
	private Dwarf getNearestPlagueable() {
		Location deathLoc = death.getLocation();
		
		Dwarf nearestDwarf = null;
		double nearestDistance = Double.MAX_VALUE;

		if (plagued.isEmpty()) {
			for (Dwarf dwarf : plagueables) {
				double distance = deathLoc.distance(dwarf.getLocation());
				if (distance < nearestDistance) {
					nearestDwarf = dwarf;
					nearestDistance = distance;
				}
			}
		}
		else {
			Iterator<Dwarf> iter = plagued.iterator();
			nearestDwarf = iter.next();
		}
		return nearestDwarf;
	}
}
