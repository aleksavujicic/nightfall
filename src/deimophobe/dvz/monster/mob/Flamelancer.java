package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Flamelancer extends SkeletonMob {
	Flamelancer(MonsterPlayer monster) {
		super(monster, MobType.FLAMELANCER);
	}
	
	private static final int ARROWS_FIRED = 15;
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		
		World world = arrow.getWorld();
		arrow.setFireTicks(10000);
		for (int i=0; i<ARROWS_FIRED; i++) {
			Arrow newArrow = world.spawnArrow(arrow.getLocation(), arrow.getVelocity(), force*2, 30f);
			newArrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			newArrow.setShooter(monster.getPlayer());
			newArrow.setCritical(arrow.isCritical());
			newArrow.setFireTicks(10000);
		}
		
		return arrow;
	}
}
