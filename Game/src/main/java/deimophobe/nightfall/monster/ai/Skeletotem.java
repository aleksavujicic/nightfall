package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.ItemManager;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.entity.GameShooter;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
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
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
	}
	
	/*
	@Override
	protected void naturalUpdate() {
		Entity target = getTarget();
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
	*/
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
	}
	
	private void skullExplosion(Location centerLoc) {
		World world = getLocation().getWorld();
		
		double kb = 0.2;
		
		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 1, 0, 0, 0);
		world.spawnParticle(Particle.SMOKE_NORMAL, centerLoc, 70, 0.5, 0.5, 0.5, 0.03);
		world.playSound(centerLoc, "entity.zombie.infect", 2, 0.75f);
		
		
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			double distance = offset.subtract(new Vector(0,1,0)).length();
			if (distance > 2.5) continue;
			
			DwarfDamage aoeDamage = dwarf.createDamage(this, GameDamageType.WITHER_SKULL, 25);
			Vector knockback = offset.normalize().multiply(kb / Math.sqrt(Math.max(1.5, distance)));
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
	
	
	@Override
	public void onDeath(MonsterDamage damage) {
		if (damage.getType() != GameDamageType.AI_REMOVER) {
			entity.getLocation().getWorld().playSound(getLocation(), "entity.witherskeleton.death", 1f, 0.6f);
		}
		super.onDeath(damage);
	}
}
