package deimophobe.dvz.monster.mob;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Flamelancer extends SkeletonMob {
	
	@Override protected MobType getType() {return MobType.FLAMELANCER;}
	@Override protected double getPower() {return 15;}
	
	Flamelancer(MonsterPlayer monster) {
		super(monster);
	}
	
	private static final int ARROWS_FIRED = 20;
	
	private static final double FLAME_CHANCE_STAND = 0.15;
	private static final double FLAME_CHANCE_ARROW = 0.35;
	
	@Override
	public void spawn() {
		super.spawn();
		getDisguise().getWatcher().setBurning(true);
		giveArrows(64);
	}
	
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		
		World world = arrow.getWorld();
		arrow.setFireTicks(10000);
		arrow.setCritical(false);
		final int arrowsToFire = (int) (ARROWS_FIRED*(force*force*force));
		for (int i=0; i<arrowsToFire; i++) {
			Arrow newArrow = world.spawnArrow(arrow.getLocation(), arrow.getVelocity(), force*2, 30f);
			newArrow.setMetadata("force", new FixedMetadataValue(Game.getGame().getPlugin(), force));
			newArrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			newArrow.setShooter(monster.getPlayer());
			newArrow.setCritical(false);
			newArrow.setFireTicks(10000);
		}
		
		Block block = monster.getPlayer().getLocation().getBlock();
		if (block.getType() == Material.AIR && Math.random() < FLAME_CHANCE_STAND) {
			block.setType(Material.FIRE);
		}
		
		return arrow;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		BlockFace face = Misc.getBlockFaceProjectileHit(proj, hitBlock);
		Block block = hitBlock.getRelative(face);
		
		if (block.getType() == Material.AIR && Math.random() < FLAME_CHANCE_ARROW) {
			block.setType(Material.FIRE);
		}
	}
	
	
}
