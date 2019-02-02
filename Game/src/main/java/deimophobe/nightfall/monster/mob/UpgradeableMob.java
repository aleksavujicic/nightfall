package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.wrappers.WrappedUpgrades;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Deimophobe on 17/01/19.
 */
abstract class UpgradeableMob<T extends WrappedUpgrades> extends AbstractMob {
	private final T upgrades;
	
	protected UpgradeableMob(MonsterPlayer monster, MobType type, Class<T> upgradeClass) {
		super(monster, type);
		
		try {
			Constructor<T> constructor = upgradeClass.getDeclaredConstructor(MonsterPlayer.class);
			constructor.setAccessible(true);
			this.upgrades = constructor.newInstance(monster);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		upgrades.addArmourModifiers(getArmour());
		upgrades.addWeaponModifiers(getWeapon());
	}
	
	protected final T getUpgrades() {
		return upgrades;
	}
	
	@Override
	protected void setWeapon(String itemName) {
		super.setWeapon(itemName);
		upgrades.addWeaponModifiers(getWeapon());
	}
}
