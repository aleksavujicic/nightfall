package deimophobe.nightfall.monster.spawnmenu;

import com.google.common.collect.Maps;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Deimophobe on 16/01/19.
 */
class UpgradeSelectMenu extends SimpleMenu<MonsterPlayer> {
	
	UpgradeSelectMenu(int size, MonsterMenuConfig config, UpgradeContainerMenu menu) {
		super(size);
		
		for (Map.Entry<MobType, UpgradeableMenuConfig> entry : config.getMenuConfigs()) {
			MobType type = entry.getKey();
			UpgradeableMenuConfig upgradeableMenuConfig = config.getMenuConfig(type);
			
			int index = upgradeableMenuConfig.getIndex();
			ItemStack itemStack = upgradeableMenuConfig.getUpgradeItem().createItemStack();
			int cost = upgradeableMenuConfig.getCost();
			UpgradeSelectMenuItem item = new UpgradeSelectMenuItem(itemStack, menu, type, cost);
			
			setItem(index, item);
		}
	}
}
