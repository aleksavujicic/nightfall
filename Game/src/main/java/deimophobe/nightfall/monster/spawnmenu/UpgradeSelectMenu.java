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
		
		Map<Integer, MobType> selectorLocations = Maps.newHashMap();
		selectorLocations.put(10, MobType.ZOMBIE_FURY);
		selectorLocations.put(11, MobType.ZOMBIE_HUSK);
		selectorLocations.put(12, MobType.ZOMBIE_SABOTEUR);
		selectorLocations.put(13, MobType.SKELETON_FLAME);
		selectorLocations.put(14, MobType.SKELETON_IMPACT);
		selectorLocations.put(15, MobType.SKELETON_WITHER);
		selectorLocations.put(16, MobType.GOBLIN_KABOOM);
		
		
		for (Map.Entry<Integer, MobType> entry : selectorLocations.entrySet()) {
			int index = entry.getKey();
			MobType type = entry.getValue();
			
			UpgradeableMenuConfig upgradeableMenuConfig = config.getMenuConfig(type);
			
			ItemStack itemStack = upgradeableMenuConfig.getUpgradeItem().createItemStack();
			int cost = upgradeableMenuConfig.getCost();
			UpgradeSelectMenuItem item = new UpgradeSelectMenuItem(itemStack, menu, type, cost);
			
			setItem(index, item);
		}
	}
}
