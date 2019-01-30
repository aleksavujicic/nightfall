package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;

/**
 * Created by Deimophobe on 17/01/19.
 */
public abstract class WrappedUpgrades {
	protected final MonsterPlayer monster;
	protected final MonsterUpgrades upgrades;
	
	WrappedUpgrades(MonsterPlayer monster) {
		this.monster = monster;
		this.upgrades = monster.getUpgrades();
	}
	
	public abstract void addWeaponModifiers(CustomItem weapon);
	public abstract void addArmourModifiers(CustomItem armour);
}
