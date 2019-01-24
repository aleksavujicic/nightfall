package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 23/01/19.
 */
public abstract class RangedUpgrades extends WrappedUpgrades {
	RangedUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	public abstract double getPower();
	public abstract int getArmourShred();
	public abstract int getArrowQuantity();
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		weapon.addModifier(ItemModifierType.POWER, (int) getPower());
		weapon.addModifier(ItemModifierType.ARMOUR_SHRED, getArmourShred());
	}
}
