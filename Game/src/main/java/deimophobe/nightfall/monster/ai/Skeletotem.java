package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.Expirable;
import deimophobe.nightfall.cooldown.RepeaterCooldown;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GameShooter;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 29/09/18.
 */
public class Skeletotem extends AIEntity<WitherSkeleton> implements GameShooter {
	
	private static final double HEALTH = 50;
	
	private static final ItemStack BOW = ItemManager.getMiscItem("ai-bow").createItemStack();
	private static final ItemStack SKULLHAND = ItemManager.getMiscItem("skeletotem-hand").createItemStack();
	
	private static final Consumer<WitherSkeleton> INITIALISER = (skeleton) -> {
		AttributeModifier speedModifier = new AttributeModifier("speed", -100, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
		skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speedModifier);
		skeleton.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.9);
		skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HEALTH);
		skeleton.setHealth(HEALTH);
		
		skeleton.getEquipment().setItemInMainHand(SKULLHAND);
		skeleton.getEquipment().setItemInOffHand(SKULLHAND);
	};
	
	
	protected Skeletotem(Location location, String name, Dwarf target) {
		super(location, name, target, WitherSkeleton.class, INITIALISER);
		Game.getGame().addUpdateable(new Shooter());
	}
	
	@Override
	public boolean isBowInstaKillable() {
		return false;
	}
	
	// Bow stuff no worky
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		ArrowMisc.setArrowDamage(arrow, 25);
		ArrowMisc.setArrowForce(arrow, 1);
		arrow.setCritical(false);
		arrow.setKnockbackStrength(1);
		arrow.setFireTicks(0);
		
		faceTarget();
		
		return null;
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock, GameEntity<?> hitEntity) {}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		if (damage.getType() == GameDamageType.MELEE) {
			damage.getMultiPartDamage().setBase(15);
			damage.multiplyKnockback(3, 1.2);
		}
		else if (damage.getType() == GameDamageType.RANGED) {
			damage.cancel();
			Projectile projectile = damage.getProjectile();
			skullExplosion(projectile.getLocation());
		}
	}
	
	
	@Override
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != GameDamageType.AI_REMOVER) {
			entity.getLocation().getWorld().playSound(getLocation(), "entity.witherskeleton.death", 1f, 0.6f);
		}
		super.onDeath(damage);
	}
	
	private class Shooter implements Expirable {
		private final Cooldown shoot = new RepeaterCooldown(25, Skeletotem.this::shoot);
		
		@Override
		public void update() {
			if (getTarget() != null) shoot.update();
		}
		
		@Override
		public boolean hasExpired() {
			return !Skeletotem.this.isAlive();
		}
		
		@Override
		public void onExpiry() {}
	}
	
	private void shoot() {
		LivingEntity target = getTarget();
		if (target == null) return;
		
		faceTarget();
		
		Location loc = this.getEyeLocation().subtract(0, 0.5, 0);
		World world = loc.getWorld();
		Vector offset = target.getLocation().subtract(this.getLocation()).toVector();
		offset.subtract(new Vector(0, 1, 0));
		offset.normalize();
		
		WitherSkull skull = world.spawn(loc, WitherSkull.class, s -> {
			s.setShooter(this.getEntity());
			s.setVelocity(offset);
		});
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!skull.isDead()) {
					skullExplosion(skull.getLocation());
					skull.remove();
				}
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 30); // 1.5 second lifetime
		resetInactivity();
	}
	
	private static final double RADIUS = 2.5;
	private static final double KB = 1.25;
	
	private void skullExplosion(Location centerLoc) {
		World world = getLocation().getWorld();
		
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 1, 0, 0, 0);
		world.spawnParticle(Particle.SMOKE_NORMAL, centerLoc, 70, 0.5, 0.5, 0.5, 0.03);
		world.playSound(centerLoc, "entity.zombie.infect", 2, 0.75f);
		
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			double distance = offset.length();
			if (distance > RADIUS) continue;
			
			final double scalingFactor = Math.min(0.15*RADIUS/distance + 0.35, 1);
			Vector knockback = offset.clone()
					.normalize()
					.multiply(KB * scalingFactor);
			
			DwarfDamage aoeDamage = dwarf.createDamage(this, GameDamageType.WITHER_SKULL, 25);
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(15);
			aoeDamage.setNoDamageTicks(8);
			aoeDamage.fire();
		}
	}
	
	private void faceTarget() {
		Entity target = getTarget();
		if (target == null) return;
		
		Vector offset = target.getLocation().subtract(this.getLocation()).toVector();
		Location newLoc = entity.getLocation().setDirection(offset);
		entity.teleport(newLoc);
	}
}
