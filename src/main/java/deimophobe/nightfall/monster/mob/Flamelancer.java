package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;
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
	
	@Override protected double getPower() {return 15;}
	
	Flamelancer(MonsterPlayer monster) {
		super(monster, MobType.FLAMELANCER);
	}
	
	private static final int ARROWS_FIRED = 15;
	
	private static final double FLAME_CHANCE_STAND = 0.15;
	private static final double FLAME_CHANCE_ARROW = 0.3;
	
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
		final int arrowsToFire = (int) (ARROWS_FIRED*(force*force));
		for (int i=0; i<arrowsToFire; i++) {
			Arrow newArrow = world.spawnArrow(arrow.getLocation(), arrow.getVelocity(), force*2, 30f);
			newArrow.setMetadata("force", new FixedMetadataValue(NightfallPlugin.getPlugin(), force));
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
	
	
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (type.isArrow()) {
			dwarf.getArmour().damage(10);
			return getPower();
		} else {
			return damage;
		}
	}
	
	
}
