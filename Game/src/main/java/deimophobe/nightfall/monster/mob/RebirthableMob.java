package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.upgrades.wrappers.RebirthableUpgrades;
import deimophobe.nightfall.monster.upgrades.wrappers.WrappedUpgrades;

import java.util.function.Function;

/**
 * Created by Deimophobe on 17/01/19.
 */
abstract class RebirthableMob<T extends RebirthableUpgrades> extends UpgradeableMob<T> {
	private final double rebirthChance;
	private final double rebirthDecrease;
	
	protected RebirthableMob(MonsterPlayer monster, MobType type, Class<T> upgradeClass) {
		super(monster, type, upgradeClass);
		
		RebirthableUpgrades upgrades = getUpgrades();
		this.rebirthChance = upgrades.getRebirthChance();
		this.rebirthDecrease = upgrades.getRebirthDecrease();
	}
	
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		if (spawnMethod == SpawnMethod.REBIRTH) {
			giveSpawnProtection(30, false, false);
		}
	}
	
	@Override
	protected void shrineProtectionDamage() {
		super.shrineProtectionDamage();
		monster.removeRebirth();
	}
	
	@Override
	public void onDeath(boolean silent) {
		super.onDeath(silent);
		
		Function<Integer, Double> chanceFunction = rebirthCount -> rebirthChance - rebirthDecrease*rebirthCount;
		monster.setRebirthSpot(monster.getLocation(), chanceFunction);
	}
}
