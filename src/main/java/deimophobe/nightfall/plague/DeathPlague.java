package deimophobe.nightfall.plague;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.DamageType;
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
					Dwarf target = getNearestPlagueable();
					death.teleport(target.getLocation());
					target.customDamage(null, DamageType.DEATH_PLAGUE, 10000);
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
