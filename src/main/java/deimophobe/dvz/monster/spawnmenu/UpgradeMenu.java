package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.*;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

/**
 * Created by Deimophobe on 2/02/17.
 */
class UpgradeMenu extends SimpleMenu<MonsterPlayer> {
	private static final int MENU_SIZE = 27;
	private final Set<String> upgrades;
	
	UpgradeMenu(ConfigurationSection section, MobType type) {
		super(MENU_SIZE);
		
		upgrades = section.getKeys(false);
		
		for (String key : section.getKeys(false)) {
			ConfigurationSection itemSection = section.getConfigurationSection(key);
			MenuItem<MonsterPlayer> item = new UpgradeMenuItem(itemSection, type);
			int index = itemSection.getInt("index");
			insertItem(index, item);
		}
	}
	
	private void insertItem(int index, MenuItem<MonsterPlayer> item) {
		MultiItem<MonsterPlayer> multiItem = (MultiItem<MonsterPlayer>) getItem(index);
		if (multiItem == null) multiItem = new MultiItem<>();
		
		multiItem.addItem(item);
		setItem(index, multiItem);
	}
	
	Set<String> getUpgrades() {
		return upgrades;
	}
}
