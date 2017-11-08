package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class VolcanicGauntlet extends AbstractBow {
	VolcanicGauntlet(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 60;
	private final static CustomItem ITEM = DwarvenItems.getBow("volcanic", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "VOLCANIC";}
	@Override public int getPower() {return POWER;}
	
	private static final double MAX_RANGE = 40;
	private static final double THICKNESS = 1.4;
	private static final double PARTICLE_OFFSET = THICKNESS/10;
	private static final double AOE_RADIUS = 1.4;
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		Location location = dwarf.getPlayer().getEyeLocation();
		Misc.moveLocation(location, 0, 0.3, -0.3);
		Vector direction = location.getDirection();

		if (!dwarf.hasArrows(3)) return null;
		dwarf.useArrows(3);
		
		double range = MAX_RANGE * force * force;
		double radius = AOE_RADIUS * force;
		
		// Show particles
		Vector delta = direction.clone().multiply(0.33);
		int times = (int) (range/0.33);
		Location particlePos = location.clone();
		World world = particlePos.getWorld();
		for (int i = 0; i<= times; i++) {
			particlePos.add(delta);
			world.spawnParticle(Particle.FLAME, particlePos, 3, PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0);
			
			// Stop beam if it hits a block
			if (particlePos.getBlock().getType().isSolid()) {
				range = location.distance(particlePos);
				break;
			}
		}
		Location feets = dwarf.getLocation().add(0, 0.25, 0);
		world.spawnParticle(Particle.FLAME, feets, (int) (30*force), 1f, 1f, 1f, 0.07);
		world.spawnParticle(Particle.FLAME, feets, (int) (100*force*force), radius/2, 0.1f, radius/2, 0);
		world.spawnParticle(Particle.LAVA, feets, (int) (20*force*force), radius/2, 0.1f, radius/2, 0);
		
		// Calculate collision
		for (GameEntity monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
			// Skip if further than distance shot or too close
			Location monsterLocation = monster.getEyeLocation();
			double distance = location.distance(monsterLocation);
			if (distance <= range) {
				// Find if close enough to beam
				Vector monsterOffset = monsterLocation.clone().subtract(location).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough damage mob
				if (monster.distanceTo(dwarf) <= radius) {
					monster.doDamage(dwarf, CustomDamageType.VOLCANIC_BOW, getPower()*force/2);
					dwarf.playSound("entity.arrow.hit_player", 0.8f, 0.5f, false);
				} else if (radialOffset <= THICKNESS) {
					monster.doDamage(dwarf, CustomDamageType.VOLCANIC_BOW, getPower()*force);
					dwarf.playSound("entity.arrow.hit_player", 0.8f, 0.5f, false);
				}
			}
		}
		
		dwarf.playSound("entity.generic.burn", 1f, 1.2f - force*0.5f, true);
		dwarf.playSound("entity.ghast.shoot", 1f, 1.35f - force*0.5f, true);
		
		return null;
	}
}

