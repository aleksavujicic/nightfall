package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.*;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class UpgradeMenu extends SimpleMenu<MonsterPlayer> {
	private static final int MENU_SIZE = 27;
	private static final String SETTINGS_KEY = "settings";
	
	public UpgradeMenu(ConfigurationSection section, SpawnMenu spawnMenu) {
		super(MENU_SIZE);
		
		MobType type = MobType.getMobType(section.getString(SETTINGS_KEY + ".mobtype"));
		
		for (String key : section.getKeys(false)) {
			if (key.equals(SETTINGS_KEY)) continue;
			
			ConfigurationSection itemSection = section.getConfigurationSection(key);
			MenuItem<MonsterPlayer> item = new UpgradeMenuItem(itemSection, type);
			int index = itemSection.getInt("index");
			insertItem(index, item);
		}
		
		setItem(0, SpawnEggMenuItem.getEgg(type));
		setItem(9, spawnMenu.getBackItem());
		setItem(18, spawnMenu.getRebirthItem());
	}
	
	private void insertItem(int index, MenuItem<MonsterPlayer> item) {
		MultiItem<MonsterPlayer> multiItem = (MultiItem<MonsterPlayer>) getItem(index);
		if (multiItem == null) multiItem = new MultiItem<>();
		
		multiItem.addItem(item);
		setItem(index, multiItem);
	}
}
