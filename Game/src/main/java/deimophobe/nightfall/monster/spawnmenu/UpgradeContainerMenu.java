package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.DynamicPageChanger;
import deimophobe.nightfall.common.menu.item.DynamicPageResetter;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.menu.submenu.DynamicMenu;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.common.menu.submenu.SubMenu;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/19.
 */
class UpgradeContainerMenu extends DynamicMenu<MonsterPlayer> {
	private final UpgradeSelectMenu upgradeSelectMenu;
	private final Map<MobType, UpgradeMenu> upgradeMenus = new HashMap<>();
	
	UpgradeContainerMenu(int size, MonsterMenuConfig config) {
		super(size);
		
		// Get Items
		ItemStack cancelStack = config.getItemStack("cancel");
		CustomItem resetTemplate = config.getItem("reset-xp");
		ItemStack resetPageStack = config.getItemStack("reset-page");
		
		// Setup reset menu
		SimpleMenu<MonsterPlayer> resetMenu = new SimpleMenu<>(size);
		MenuItem<MonsterPlayer> cancelItem = new DynamicPageResetter<>(cancelStack, this);
		MenuItem<MonsterPlayer> resetItem = new ResetItem(resetTemplate, this);
		resetMenu.setItem(11, resetItem);
		resetMenu.setItem(15, cancelItem);
		
		MenuItem<MonsterPlayer> resetPage = new DynamicPageChanger<>(resetPageStack, this, resetMenu);
		for (MobType mobType : MobType.values()) {
			if (!mobType.isUpgradeable()) continue;
			
			UpgradeMenu upgradeMenu = new UpgradeMenu(size, config, mobType);
			upgradeMenu.setItem(8, resetPage);
			upgradeMenus.put(mobType, upgradeMenu);
		}
		
		upgradeSelectMenu = new UpgradeSelectMenu(size, config,this);
	}
	
	@NotNull
	@Override
	protected SubMenu<MonsterPlayer> getMenu(MenuSession<MonsterPlayer> session) {
		MonsterUpgrades upgrades = session.getData().getUpgrades();
		MobType primary = upgrades.getPrimaryMob();
		
		if (primary == null) return upgradeSelectMenu;
		return upgradeMenus.get(primary);
	}
	
}
