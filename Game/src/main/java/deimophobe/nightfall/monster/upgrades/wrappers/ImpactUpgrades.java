package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 23/01/19.
 */
public class ImpactUpgrades extends RangedUpgrades {
	ImpactUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	@Override
	public int getArrowQuantity() {
		return 0;
	}
	
	@Override
	public void addWeaponModifiers(CustomItem weapon) {
		
	}
	
	@Override
	public void addArmourModifiers(CustomItem armour) {
	
	}
}
