package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Display;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArrowMisc;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonWither extends AbstractToggleSkeleton {

	private final int piercing;
	private final int sniper;
	private final double siphon;
	private final boolean withering;
	private final double realArrowRes;
	@Update @Display private final ComplexCooldown sniperCD = new ComplexCooldown(8*20);

	private static final Integer[] ARROW_RES_VALUES = {0, 10, 20, 30, 40, 50};

	SkeletonWither(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.wither"));
		
		this.piercing = upgrades.get("piercing");
		this.sniper = upgrades.get("sniper");
		this.siphon = upgrades.get("siphon");
		int arrowRes = ARROW_RES_VALUES[upgrades.get("arrowres-wither")];
		int extraHealth = upgrades.get("extrahealth-wither");
		this.withering = (upgrades.get("withering") > 0);
		this.realArrowRes = arrowRes * 0.01;

		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, extraHealth * 3, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, piercing * 5);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		if (withering) forceBowToggle(true);
	}
	
	@Override
	protected boolean canToggle() {
		return withering;
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || (damage.getType() == GameDamageType.WITHER_SKULL)) {
			sniperCD.reset();
			monster.heal(siphon);
			
			if (withering) {
				damage.getDwarf().givePotionEffect(PotionEffectType.WITHER, 50, 2, true, false, false);
			}
		}
	}

	@Override
	public void onProjectileLand(Projectile proj, Block block, Entity hitEntity) {
		super.onProjectileLand(proj, block, hitEntity);
		if (proj.getType() == EntityType.WITHER_SKULL) {
			skullExplosion(proj.getLocation());
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (isToggled() && monster.hasItem(Material.ARROW, 2)) {
			if (force < 0.5) return null;
			
			monster.useItem(Material.ARROW, 2);

			Location loc = monster.getEyeLocation();
			World world = loc.getWorld();

			WitherSkull skull = (WitherSkull) world.spawnEntity(loc, EntityType.WITHER_SKULL);
			skull.setShooter(monster.getPlayer());
			skull.setVelocity(loc.getDirection().multiply(0.7*force*force*force));

			new BukkitRunnable() {
				@Override
				public void run() {
					if (!skull.isDead()) {
						skullExplosion(skull.getLocation());
						skull.remove();
					}
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), 30); // 1.5 second lifetime

			world.playSound(loc, "entity.wither.break_block", 0.1f, 0.8f);

			((SkeletonWatcher) getDisguise().getWatcher()).setSwingArms(false);

			return null;
		} else {
			return super.onBowFire(arrow, force);
		}
	}

	private void skullExplosion(Location centerLoc) {
		World world = monster.getLocation().getWorld();

		double kb = 0.1;

		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 1, 0, 0, 0);
		world.spawnParticle(Particle.SMOKE_NORMAL, centerLoc, 70, 0.5, 0.5, 0.5, 0.03);
		world.playSound(centerLoc, "entity.zombie.infect", 2, 0.75f);


		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			double distance = offset.subtract(new Vector(0,1,0)).length();
			if (distance > 2) continue;

			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, GameDamageType.WITHER_SKULL, getPower());
			Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, distance)));
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(getArmourShred());
			aoeDamage.fire();
		}
	}
	
	private boolean sniperActive() {
		if (sniperCD == null) return false; // Needed because Skeleton.<init> calls getPower()
		return !sniperCD.isAvailable();
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(realArrowRes);
	}

	@Override
	protected int getPower() {
		if (sniperActive()) {
			return super.getPower() * (10 + sniper) / 10;
		}
		else {
			return super.getPower();
		}
	}

	@Override
	protected int getArmourShred() {
		if (sniperActive()) {
			return (super.getArmourShred() + piercing * 5) * (10 + sniper) / 10;
		}
		else {
			return (super.getArmourShred() + piercing * 5);
		}
	}
}
