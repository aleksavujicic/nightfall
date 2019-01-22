package deimophobe.nightfall.monster.upgrades;

import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.Mob;
import deimophobe.nightfall.monster.mob.MobType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/19.
 */
public class MonsterUpgrades {
	private static final String PRIMARY_VALUE_STRING = "value";
	
	private final MonsterPlayer monster;
	
	private final Map<Upgrade, Integer> upgradeLevels;
	private int amountSpent = 0;
	
	private MobType primaryMob = null;
	
	public MonsterUpgrades(MonsterPlayer monster) {
		this.monster = monster;
		this.upgradeLevels = new HashMap<>();
	}
	
	
	// ----- Upgrade Levels -----
	
	public Map<Upgrade, Integer> getUpgradeLevels() {
		return upgradeLevels;
	}
	
	public boolean hasUpgrade(Upgrade upgrade) {
		return getLevel(upgrade) >= 1;
	}
	
	public int getLevel(Upgrade upgrade) {
		return upgradeLevels.computeIfAbsent(upgrade, u -> 0);
	}
	
	public void setLevel(Upgrade upgrade, int level) {
		upgradeLevels.put(upgrade, level);
	}
	
	private Object getValue(Upgrade upgrade, String valueName) {
		int level = getLevel(upgrade);
		return upgrade.getValue(valueName, level);
	}
	
	public int getIntegerValue(Upgrade upgrade, String valueName) {
		Object value = getValue(upgrade, valueName);
		if (value == null) return 0;
		
		return (int) value;
	}
	
	public double getDoubleValue(Upgrade upgrade, String valueName) {
		Object value = getValue(upgrade, valueName);
		if (value == null) return 0;
		
		if (value instanceof Integer) {
			// Need to unbox before casting (casting Integer directly to double throws a cast exception)
			return (double) (int) value;
		} else {
			return (double) value;
		}
	}
	
	public int getTickValue(Upgrade upgrade,  String valueName) {
		return getTickValue(upgrade, valueName, false);
	}
	
	public int getTickValue(Upgrade upgrade,  String valueName, boolean allowDoubles) {
		if (allowDoubles) {
			return (int) (getDoubleValue(upgrade, valueName) * 20);
		} else {
			return getIntegerValue(upgrade, valueName) * 20;
		}
	}
	
	public double getFractionalValue(Upgrade upgrade, String valueName) {
		return getDoubleValue(upgrade, valueName) / 100;
	}
	
	public int getIntegerValue(Upgrade upgrade) {
		return getIntegerValue(upgrade, PRIMARY_VALUE_STRING);
	}
	
	public double getDoubleValue(Upgrade upgrade) {
		return getDoubleValue(upgrade, PRIMARY_VALUE_STRING);
	}
	
	public double getFractionalValue(Upgrade upgrade) {
		return getFractionalValue(upgrade, PRIMARY_VALUE_STRING);
	}
	
	
	// ----- Purchasing Upgrades -----
	
	public boolean tryPurchaseUpgrade(Upgrade upgrade) {
		if (!upgrade.upgradesMeetPrerequisites(upgradeLevels)) return false;
		
		int currentLevel = getLevel(upgrade);
		boolean upgradeable = upgrade.canUpgrade(currentLevel);
		if (!upgradeable) return false;
		
		int nextCost = upgrade.getCost(currentLevel + 1);
		if (!monster.hasExperience(nextCost)) {
			monster.sendInsufficientExperienceMessage(nextCost);
			return false;
		}
		
		monster.useExperience(nextCost, false);
		amountSpent += nextCost;
		upgradeLevels.compute(upgrade, (u, v) -> v + 1);
		monster.sendDebugMsg("Bought upgrade " + upgrade);
		return true;
	}
	
	public void resetUpgrades(double refundRate) {
		upgradeLevels.clear();
		monster.forceGiveExperience((int) (refundRate * amountSpent));
		amountSpent = 0;
		primaryMob = null;
		monster.sendDebugMsg("Reset upgrades");
	}
	
	public int getAmountSpent() {
		return amountSpent;
	}
	
	public void increaseAmountSpent(int amount) {
		amountSpent += amount;
	}
	
	
	// ----- Primary Mob -----
	
	public void setPrimaryMob(MobType primaryMob) {
		this.primaryMob = primaryMob;
		monster.sendDebugMsg("Set primary mob: " + primaryMob);
	}
	
	public MobType getPrimaryMob() {
		return primaryMob;
	}
	
	public Mob createPrimaryMob() {
		if (primaryMob == null) return MobType.ZOMBIE_BASE.createMob(monster);
		return primaryMob.createMob(monster);
	}
}
