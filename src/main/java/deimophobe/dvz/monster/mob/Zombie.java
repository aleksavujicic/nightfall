package deimophobe.dvz.monster.mob;

import deimophobe.dvz.items.modifiers.ItemModifierType;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.upgrade.MobUpgrade;

/**
 * Created by Deimophobe on 2/02/17.
 */
class Zombie extends AbstractTypedMob {
	
	@Override protected MobType getType() {return MobType.ZOMBIE;}
	
	protected Zombie(MonsterPlayer mons) {
		super(mons);
		MobUpgrade upgrades = monster.getUpgrades(MobType.ZOMBIE);
		int attack = upgrades.getUpgrade("attack");
		int health = upgrades.getUpgrade("health");
		getWeapon().addModifier(ItemModifierType.ATTACK, attack, "Upgrade");
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
	}
}
