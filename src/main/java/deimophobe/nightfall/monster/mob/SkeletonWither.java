package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonWither extends Skeleton {

	private static final double MAX_RANGE = 30;
	private static final double THICKNESS = 1.25;
	private static final Consumer<Location> PARTICLE_PLACER =
			(location) -> location.getWorld().spawnParticle(Particle.REDSTONE, location, 0, 40d/256, 8d/256, 70d/256, 1);

	private int damageBoost = 0;
	private int piercing;
	private int damageBooster;
	private int siphon;
	private int arrowRes;
	private int extraHealth;
	private int withering;
	private double realArrowRes = 0;

	private static Integer[] arrowResValues = {0, 10, 20, 30, 40, 50};

	SkeletonWither(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.wither"));
		this.piercing = upgrades.get("piercing");
		this.damageBooster = upgrades.get("sniper");
		this.siphon = upgrades.get("siphon");
		this.arrowRes = arrowResValues[upgrades.get("arrowres-wither")];
		this.extraHealth = upgrades.get("extrahealth-wither");
		this.withering = upgrades.get("withering");
		realArrowRes = arrowRes * 0.01;

		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, extraHealth * 3, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, piercing * 5);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (halfSec && damageBoost > 0)
			damageBoost = Math.max(damageBoost - 1, 0);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || (damage.getType() == CustomDamageType.WITHER_BEAM)) {
			damageBoost = Math.min(damageBoost + 5 + damageBooster, 20);
			monster.heal(this.siphon);
			if (withering >= 1) {
				damage.getDwarf().givePotionEffect(PotionEffectType.WITHER, 50, 2, true, false, false);
			}
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (withering >= 1 && monster.hasItem(Material.ARROW)) {
			if (force < 0.7 ) {
				return null;
			}

			for (int i = 0; i < 3; i++) {
				try {
					monster.forceUseItem(Material.ARROW);
				} catch (NullPointerException exception) {
					// Bad style to use try catch blocks like this, but want withers to be able to shoot when they have arrows
				}
			}

			double range = MAX_RANGE * force * force;
			GamePlayer.GameEntityDamager<Dwarf> entityDamager = monster.new GameEntityDamager(CustomDamageType.WITHER_BEAM, getPower()*force*force);
			monster.fireBeam(range, THICKNESS, 0.3, PARTICLE_PLACER, entityDamager, null);
			((SkeletonWatcher) getDisguise().getWatcher()).setSwingArms(false);

			return null;
		} else {
			return super.onBowFire(arrow, force);
		}
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(realArrowRes);
	}

	@Override
	protected int getPower() {
		return super.getPower() + damageBoost;
	}

	@Override
	protected int getArmourShred() {
		return super.getArmourShred() + damageBoost + piercing * 5;
	}
}
