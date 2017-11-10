package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class SkeletonWither extends Skeleton {

	private int damageBoost = 0;
	private int piercing;
	private int damageBooster;
	private int siphon;
	private int arrowRes;
	private int extraHealth;
	private int withering;
	
	SkeletonWither(MonsterPlayer monster) {
		super(monster, MobData.getMobData("skeleton.wither"));
		this.piercing = upgrades.get("piercing");
		this.damageBooster = upgrades.get("sniper");
		this.siphon = upgrades.get("siphon");
		this.arrowRes = upgrades.get("arrowres-wither");
		this.extraHealth = upgrades.get("extrahealth-wither");
		this.withering = upgrades.get("withering");
		realArrowRes = arrowRes * 0.01;

		getArmour().addModifier(ItemModifierType.ARROW_RESISTANCE, arrowRes * 10, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, extraHealth * 3, "Upgrade");
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, piercing * 5 + withering * 15);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (sec && damageBoost > 0)
			damageBoost = Math.max(damageBoost - 1, 0);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) {
			damageBoost = Math.min(damageBoost + 2 * damageBooster, 30);
			monster.heal(this.siphon);
			if (withering >= 1) {
				damage.getDwarf().givePotionEffect(PotionEffectType.WITHER, 50, 2, true, false, false);
			}
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
		return super.getArmourShred() + damageBoost + piercing * 5 + withering * 15;
	}
}
