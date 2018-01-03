package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArrowMisc;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
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
	private final int piercing;
	private final int damageBooster;
	private final double siphon;
	private final boolean withering;
	private final double realArrowRes;

	private static final Integer[] ARROW_RES_VALUES = {0, 10, 20, 30, 40, 50};

	SkeletonWither(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.wither"));
		
		this.piercing = upgrades.get("piercing");
		this.damageBooster = upgrades.get("sniper");
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
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (halfSec && damageBoost > 0)
			damageBoost--;
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || (damage.getType() == CustomDamageType.WITHER_BEAM)) {
			damageBoost = Math.min(damageBoost + 5 + damageBooster, 20);
			monster.heal(siphon);
			
			if (withering) {
				damage.getDwarf().givePotionEffect(PotionEffectType.WITHER, 50, 2, true, false, false);
			}
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		if (withering && monster.hasItem(Material.ARROW, 2)) {
			if (force < 0.7) return null;
			
			monster.useItem(Material.ARROW, 2);

			double range = MAX_RANGE * force * force;
			GamePlayer.GameEntityDamager<Dwarf> entityDamager = monster.new GameEntityDamager<Dwarf>(CustomDamageType.WITHER_BEAM, getPower()*force*force, 2);
			monster.fireParticle(2, range, THICKNESS, 0.3, PARTICLE_PLACER, entityDamager, null);
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
