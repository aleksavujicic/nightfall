package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Spiderling extends AbstractMob {
	
	private static final double CORRODE_DISTANCE = 15;
	@Update private final ComplexCooldown spitter = new ComplexCooldown(8, this::spit);
	
	Spiderling(MonsterPlayer monster) {
		super(monster, MobType.SPIDERLING);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, 3);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isPlayerHoldingWeapon()) {
			spitter.tryUse();
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.getReceiver().givePotionEffect(PotionEffectType.POISON, 50, 3, true, false, true);
		damage.multiplyKnockback(0.5);
	}
	
	private void spit() {
		Location loc = monster.getLocation();
		World world = loc.getWorld();
		
		Snowball snow = world.spawn(loc.add(0,0.25,0), Snowball.class);
		snow.setShooter(monster.getPlayer());
		snow.setVelocity(loc.getDirection().add(new Vector(0,0.25,0)));
		world.playSound(loc, "entity.spider.step", 0.3f, 1);
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock, Entity hitEntity) {
		if (proj.getLocation().distance(monster.getLocation()) <= CORRODE_DISTANCE)
			BlockConverter.convert(BlockConverter.Type.CORROSION, proj.getLocation(), 2);
	}
	
	
	@Update private final Updateable climbing = () -> {
		if (monster.getPlayer().isSneaking() && nextToWall()) {
			monster.givePermanentPotionEffect(PotionEffectType.LEVITATION, 3);
		} else {
			if (!monster.isFrozen()) monster.removePotionEffect(PotionEffectType.LEVITATION);
		}
	};
	
	private static final Vector[] OFFSET_CHECKS = new Vector[] {
			new Vector(0.8,0,0),
			new Vector(-0.8,0,0),
			new Vector(0,0,0.8),
			new Vector(0,0,-0.8),
			new Vector(0.6,0,0.6),
			new Vector(-0.6,0,0.6),
			new Vector(0.6,0,-0.6),
			new Vector(-0.6,0,-0.6),
			new Vector(0.8,1,0),
			new Vector(-0.8,1,0),
			new Vector(0,1,0.8),
			new Vector(0,1,-0.8),
			new Vector(0.6,1,0.6),
			new Vector(-0.6,1,0.6),
			new Vector(0.6,1,-0.6),
			new Vector(-0.6,1,-0.6),
			new Vector(0,2,0),
			new Vector(0.8,2,0),
			new Vector(-0.8,2,0),
			new Vector(0,2,0.8),
			new Vector(0,2,-0.8),
			new Vector(0.6,2,0.6),
			new Vector(-0.6,2,0.6),
			new Vector(0.6,2,-0.6),
			new Vector(-0.6,2,-0.6),
	};
	
	private boolean nextToWall() {
		Location location = monster.getLocation();
		for (Vector offset : OFFSET_CHECKS) {
			if (location.clone().add(offset).getBlock().getType().isSolid())
				return true;
		}
		return false;
	}
}
