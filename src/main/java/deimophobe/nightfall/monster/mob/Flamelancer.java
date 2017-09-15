package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

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
	public void onSpawn() {
		super.onSpawn();
		getDisguise().getWatcher().setBurning(true);
		giveArrows(64);
	}
	
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		
		arrow.setFireTicks(10000);
		arrow.setCritical(false);
		final int arrowsToFire = (int) (ARROWS_FIRED*(force*force));
		for (int i=0; i<arrowsToFire; i++) {
			Arrow newArrow = ArrowMisc.summonArrow(monster, getPower(), force*2, force, 30f);
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
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == NaturalDamageType.RANGED) {
			damage.setArmourShred(10);
		}
	}
	
	
}
