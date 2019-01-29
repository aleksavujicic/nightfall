package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import deimophobe.nightfall.monster.upgrades.UpgradeRegistry;

import java.util.Collection;

/**
 * Created by Deimophobe on 2/02/17.
 */
class UpgradeMenu extends SimpleMenu<MonsterPlayer> {
    UpgradeMenu(int size, MonsterMenuConfig config, MobType type) {
        super(size);
        
	    UpgradeRegistry registry = config.getRegistry();
	    Collection<Upgrade> upgrades = registry.getUpgrades(type);
	    
	    for (Upgrade upgrade : upgrades) {
	    	UpgradeMenuItem upgradeMenuItem = new UpgradeMenuItem(upgrade);
	    	int index = upgrade.getIndex();
	    	this.setItem(index, upgradeMenuItem);
	    }
	
	    UpgradeableMenuConfig upgradeConfig = config.getMenuConfig(type);
	    CustomItem spawnItem = upgradeConfig.getSpawnItem();
	    MenuItem<MonsterPlayer> spawnEgg = new PermanentSpawnEgg(spawnItem.createItemStack(), type);
	    MenuItem<MonsterPlayer> rebirth = config.getRebirthItem();
	    
	    setItem(0, spawnEgg);
	    if (upgradeConfig.hasRebirth()) {
	    	setItem(9, rebirth);
	    }
    }
}
