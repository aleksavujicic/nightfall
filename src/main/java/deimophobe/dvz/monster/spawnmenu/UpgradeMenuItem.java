package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.items.lore.LoreTemplate;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import deimophobe.dvz.monster.upgrade.MobUpgrade;
import deimophobe.dvz.monster.upgrade.UpgradeApplyOperation;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem extends CostMobMenuItem {
	
	private final String name;
	private final Collection<String> prereqs;
	private final MobType type;
	
	private final boolean permanent;
	
	private final String upgradeType;
	private final UpgradeApplyOperation upgradeOper;
	private final int upgradeValue;
	
	
	private static ItemStack getItem(ConfigurationSection config) {
		CustomItem item = CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.MOB_UPGRADE, Slot.MAIN_HAND);
		int cost = config.getInt("cost");
		item.applyVariable("cost", ""+cost);
		return item.createItemStack();
	}
	
	UpgradeMenuItem(ConfigurationSection config, MobType type) {
		super(getItem(config), config.getInt("cost"));
		
		this.name = config.getName();
		this.prereqs = config.getStringList("prereq");
		this.type = type;
		this.permanent = config.getBoolean("permanent", false);
		
		
		this.upgradeType = config.getString("upgrade.type");
		this.upgradeOper = UpgradeApplyOperation.getOperation(config.getString("upgrade.operation", "increment"));
		this.upgradeValue = config.getInt("upgrade.value",1);
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer monster) {
		MobUpgrade upgrades = monster.getUpgrades(type);
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
