package deimophobe.nightfall.monster.upgrades.wrappers;

import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 23/01/19.
 */
public abstract class RangedUpgrades extends WrappedUpgrades {
	RangedUpgrades(MonsterPlayer monster) {
		super(monster);
	}
	
	public abstract int getArrowQuantity();
}
