package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.Menu;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class UpgradeMenu extends Menu<MonsterPlayer> {
	
	public UpgradeMenu(String title, MobType type, ConfigurationSection section) {
		super(title, 3);
		
		for (String key : section.getKeys(false)) {
			ConfigurationSection itemSection = section.getConfigurationSection(key);
			MenuItem<MonsterPlayer> item = new UpgradeMenuItem(itemSection, type);
			int index = itemSection.getInt("index");
			addItem(index, item);
		}
		
		setItem(0, SpawnEgg.getEgg(type));
		setItem(18, BackMenuItem.getBackMenuItem());
	}
}
