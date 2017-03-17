package deimophobe.dvz.plague;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.MapManager;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 10/03/17.
 */
class DeathPlague extends Plague {
	private Enderman death;
	
	@Override
	protected void onStart() {
		World world = MapManager.getManager().getWorld();
		
		Location spawnLoc = ShrineManager.getManager().getDwarfSpawn().clone();
		spawnLoc.setY(0);
		
		death = (Enderman) world.spawnEntity(spawnLoc, EntityType.ENDERMAN);
		death.setMetadata("death", new FixedMetadataValue(Game.getGame().getPlugin(), true));
		death.setAI(false);
		death.setInvulnerable(true);
		death.setCollidable(false);
		death.setSilent(true);
		death.setRemoveWhenFarAway(false);
		world.playSound(ShrineManager.getManager().getDwarfSpawn(), Sound.ENTITY_ENDERMEN_STARE, 100, 1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (toKill > 0) {
					Dwarf target = getNearestPlagueable();
					death.teleport(target.getLocation());
					target.customDamage(null, DamageType.DEATH_PLAGUE, 10000);
					world.playSound(target.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1);
					world.playSound(target.getLocation(), "entity.endermen.scream", 1, 1);
					removeDwarf(target);
				} else {
					this.cancel();
					death.remove();
					endPlague();
				}
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 160, 40);
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
}
