package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.DudCooldown;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArrowMisc;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonWither extends Skeleton {

	private final int piercing;
	private final int sniper;
	private final double siphon;
	private final boolean withering;
	private final double realArrowRes;
	private int sniperCD;
	private static final int MAX_SNIPER_CD = 160;

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
		this.sniperCD = 0;

		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, extraHealth * 3, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, piercing * 5);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (sniperCD > 0){
			sniperCD--;
		}
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || (damage.getType() == CustomDamageType.WITHER_SKULL)) {
			sniperCD = MAX_SNIPER_CD;
			monster.heal(siphon);
			
			if (withering) {
				damage.getDwarf().givePotionEffect(PotionEffectType.WITHER, 50, 2, true, false, false);
			}
		}
	}

	@Override
	public void onProjectileLand(Projectile proj, Block block, Entity hitEntity) {
		super.onProjectileLand(proj, block, hitEntity);
		if (withering) {
			skullExplosion(proj.getLocation());
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (withering && monster.hasItem(Material.ARROW, 2)) {
			if (force < 0.5) return null;
			
			monster.useItem(Material.ARROW, 2);

			Location loc = monster.getEyeLocation();
			World world = loc.getWorld();

			WitherSkull skull = (WitherSkull) world.spawnEntity(loc, EntityType.WITHER_SKULL);
			skull.setShooter(monster.getPlayer());
			skull.setVelocity(loc.getDirection().multiply(0.8*force*force*force));

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

		double kb = 0.4;

		world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 1, 0, 0, 0);
		world.spawnParticle(Particle.SMOKE_NORMAL, centerLoc, 70, 0.5, 0.5, 0.5, 0.03);
		world.playSound(centerLoc, "entity.zombie.infect", 2, 0.75f);


		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getEyeLocation().subtract(centerLoc).toVector();
			double distance = offset.subtract(new Vector(0,1,0)).length();
			if (distance > 3) continue;

			DwarfDamage aoeDamage = dwarf.createDamage(this.monster, CustomDamageType.WITHER_SKULL, getPower());
			Vector knockback = offset.multiply(kb / Math.sqrt(Math.max(2, distance)) );
			knockback.setY(knockback.getY() / 2 + 0.1);
			aoeDamage.setKnockback(knockback);
			aoeDamage.setArmourShred(getArmourShred());
			aoeDamage.fire(true);
		}
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(realArrowRes);
	}

	@Override
	protected int getPower() {
		if (sniperCD > 0) {
			return super.getPower() * (10 + sniper) / 10;
		}
		else {
			return super.getPower();
		}
	}

	@Override
	protected int getArmourShred() {
		if (sniperCD > 0) {
			return (super.getArmourShred() + piercing * 5) * (10 + sniper) / 10;
		}
		else {
			return (super.getArmourShred() + piercing * 5);
		}
	}

	@Override
	public float getCooldown() {
		return (float) sniperCD/MAX_SNIPER_CD;
	}
}
