package deimophobe.nightfall.dwarf.kit.elements;

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
public class VolcanicBow extends AbstractBow {
	VolcanicBow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 40;
	private final static CustomItem ITEM = DwarvenItems.getBow("volcanic", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "VOLCANIC";}
	@Override public int getPower() {return POWER;}
	
	private static final double MAX_RANGE = 20;
	private static final double THICKNESS = 1.5;
	private static final double PARTICLE_OFFSET = THICKNESS/10;
	private static final double AOE_RADIUS = 2;
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		Location location = dwarf.getPlayer().getEyeLocation();
		double yaw = location.getYaw() * Math.PI/180;
		location.add(-0.3*Math.cos(yaw), -0.3, -0.3*Math.sin(yaw));
		Vector direction = location.getDirection();
		
		// This code makes it so that if force is >= 0.5, then it requires
		// an extra arrow to fire (and a further arrow when >=0.8),
		if (!dwarf.hasArrows(2))
			force = Math.min(force, 0.4f);
		
		if (!dwarf.hasArrows(3))
			force = Math.min(force, 0.7f);
		
		if (force >= 0.4f)
			dwarf.useArrow();
		if (force >= 0.7f)
			dwarf.useArrow();
		
		double range = MAX_RANGE * force * force;
		
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
		world.spawnParticle(Particle.FLAME, dwarf.getLocation(), (int) (200*force), 1f, 0.5f, 1f, 0.07);
		
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
				if (radialOffset <= THICKNESS || monster.distanceTo(dwarf) <= AOE_RADIUS) {
					monster.doDamage(dwarf, CustomDamageType.VOLCANIC_BOW, getPower()*force);
				}
			}
		}
		
		dwarf.playSound("entity.generic.burn", 1f, 0.7f, true);
		dwarf.playSound("entity.ghast.shoot", 1f, 0.85f, true);
		
		return null;
	}
}

