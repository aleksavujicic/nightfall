package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem extends CostMobMenuItem {
	
	UpgradeMenuItem(ConfigurationSection config, UpgradeMenu menu) {
		super(ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND), config.getInt("cost"));
		
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer player) {
		return false;
	}
	
	@Override
	protected boolean onPayCost(MonsterPlayer monster) {
		return false;
	}
}
