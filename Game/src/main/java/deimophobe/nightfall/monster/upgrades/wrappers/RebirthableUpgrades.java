package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 22/01/19.
 */
public abstract class RebirthableUpgrades extends WrappedUpgrades {
	
	RebirthableUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	public abstract double getRebirthChance();
	public abstract double getRebirthDecrease();
}
