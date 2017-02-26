package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.upgrade.Upgrades;
import deimophobe.dvz.monster.upgrade.UpgradeApplyOperation;
import deimophobe.dvz.monster.upgrade.UpgradeType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem extends CostMobMenuItem {
	
	private final String name;
	private final Collection<String> prereqs;
	private final MobType type;
	
	private final boolean permanent;
	
	private final UpgradeType upgradeType;
	private final UpgradeApplyOperation upgradeOper;
	private final int upgradeValue;
	
	
	UpgradeMenuItem(ConfigurationSection config, MobType type, String name) {
		super(ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND), config.getInt("cost"));
		
		this.name = name;
		this.prereqs = config.getStringList("prereq");
		this.type = type;
		this.permanent = config.getBoolean("permanent", false);
		
		this.upgradeType = UpgradeType.getUpgradeType(config.getString("upgrade.type"));
		this.upgradeOper = UpgradeApplyOperation.getOperation(config.getString("upgrade.operation"));
		this.upgradeValue = config.getInt("upgrade.value");
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer monster) {
		Upgrades upgrades = monster.getUpgrades(type);
		for (String prereq : prereqs) {
			if (!upgrades.hasLabel(prereq))
				return false;
		}
		
		if (!permanent && upgrades.hasLabel(name))
			return false;
		
		return true;
	}
	
	@Override
	protected boolean onPayCost(MonsterPlayer monster) {
		if (permanent)
			monster.getUpgrades(type).applyUppgrade(upgradeType, upgradeOper, upgradeValue);
		else
			monster.getUpgrades(type).applyUppgrade(upgradeType, upgradeOper, upgradeValue, name);
		return true;
	}
}
