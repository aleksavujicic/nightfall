package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.PlayerMonster;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import deimophobe.dvz.monster.mob.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Ebow extends Bow {
	Ebow(Dwarf dwarf) {
		super(dwarf, BowType.EBOW, 15);
	}
	
	private static final double MAX_RANGE = 15;
	private static final double THICKNESS = 1.5;
	private static final double MIN_DISTANCE_FROM_SHOOTER = 3;
	private static final double PROC_RADIUS = 3;
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		
		Location dwarfLocation = dwarf.getPlayer().getEyeLocation();
		Vector direction = dwarfLocation.getDirection();
		
		double range = MAX_RANGE * force;
		
		// Show particles
		Vector delta = direction.clone().multiply(0.5);
		int times = (int) (range/0.5);
		Location particlePos = dwarfLocation.clone();
		World world = particlePos.getWorld();
		for (int i = 0; i<= times; i++) {
			particlePos.add(delta);
			world.spawnParticle(Particle.VILLAGER_HAPPY, particlePos, 3, 0.1, 0.1, 0.1);
			
			// Stop beam if it hits a block
			if (particlePos.getBlock().getType().isSolid()) {
				range = dwarfLocation.distance(particlePos);
				break;
			}
		}
		
		// Calculate collision
		for (PlayerMonster monster : MobManager.getManager().getMobs()) {
			// Skip if further than distance shot or too close
			Location monsterLocation = monster.getPlayer().getEyeLocation();
			double distance = dwarfLocation.distance(monsterLocation);
			if (distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = monsterLocation.clone().subtract(dwarfLocation).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough damage mob
				if (radialOffset <= THICKNESS) {
					monster.damage(power, dwarf);
					
					// Give procs to nearby dwarves
					if (distance >= MIN_DISTANCE_FROM_SHOOTER) {
						for (Dwarf procDwarf : DwarfManager.getManager().getDwarves()) {
							if (monsterLocation.distance(procDwarf.getLocation()) <= PROC_RADIUS)
								procDwarf.giveProc(Dwarf.ProcType.EBOW);
						}
					}
				}
			}
		}
		
		// Same as above but for ais
		for (AIEntity ai : AIManager.getManager().getAIs()) {
			// Skip if further than distance shot or too close
			Location monsterLocation = ai.getEntity().getEyeLocation();
			double distance = dwarfLocation.distance(monsterLocation);
			if (distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = monsterLocation.clone().subtract(dwarfLocation).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough damage mob
				if (radialOffset <= THICKNESS) {
					ai.getEntity().damage(power);
					if (ai.getEntity().isDead())
						dwarf.onKill(ai, DamageType.BOW);
					
					// Give procs to nearby dwarves
					if (distance >= MIN_DISTANCE_FROM_SHOOTER) {
						for (Dwarf procDwarf : DwarfManager.getManager().getDwarves()) {
							if (monsterLocation.distance(procDwarf.getLocation()) <= PROC_RADIUS)
								procDwarf.giveProc(Dwarf.ProcType.EBOW);
						}
					}
				}
			}
		}
		
		return null;
	}
}
