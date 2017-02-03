package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem extends CostMobMenuItem {
	
	private final String name;
	private final Collection<String> prereqs;
	private final MobType type;
	
	UpgradeMenuItem(ConfigurationSection config, MobType type) {
		super(ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND), config.getInt("cost"));
		
		this.name = config.getString("name");
		this.prereqs = config.getStringList("prereq");
		this.type = type;
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer monster) {
		if (monster.hasUpgrade(type, name))
			return false;
		
		for (String prereq : prereqs) {
			if (!monster.hasUpgrade(type, prereq))
				return false;
		}
		
		return true;
	}
	
	@Override
	protected boolean onPayCost(MonsterPlayer monster) {
		monster.addUpgrade(type, name);
		return true;
	}
}
