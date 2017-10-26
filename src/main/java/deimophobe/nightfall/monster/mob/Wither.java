package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Wither extends SkeletonMob {
	
	@Override protected double getPower() {return 30;}
	private double damageBoost = 0;
	
	Wither(MonsterPlayer monster) {
		super(monster, MobType.WITHER);
		getArmour().addModifier(ItemModifierType.SPEED, -10, "Wither");
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		giveArrows(128);
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
			damageBoost = Math.min(damageBoost + 8, 20);
			damage.setArmourShred((int) damageBoost*3 + 35);
			damage.getDamage().addBoost(damageBoost);
			monster.heal(5);
		}
	}
}
