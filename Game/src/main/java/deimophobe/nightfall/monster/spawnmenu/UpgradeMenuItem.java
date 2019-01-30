package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.MonsterUpgrades;
import deimophobe.nightfall.monster.upgrades.Upgrade;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 3/02/17.
 */
class UpgradeMenuItem implements MenuItem<MonsterPlayer> {
	
	private final Upgrade upgrade;
	
	UpgradeMenuItem(Upgrade upgrade) {
		this.upgrade = upgrade;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		MonsterUpgrades upgrades = monster.getUpgrades();
		
		if (isHidden(upgrades)) return null;
		int nextLevel = upgrades.getLevel(upgrade) + 1;
		
		return upgrade.getItem(nextLevel);
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		MonsterUpgrades upgrades = monster.getUpgrades();
		
		if (isHidden(upgrades)) return false;
		return upgrades.tryPurchaseUpgrade(upgrade);
	}


	private boolean isHidden(MonsterUpgrades upgrades) {
		Map<Upgrade, Integer> upgradeLevels = upgrades.getUpgradeLevels();
		int currentLevel = upgrades.getLevel(upgrade);
		
		return !upgrade.upgradesMeetPrerequisites(upgradeLevels)
				|| !upgrade.canUpgrade(currentLevel);
	}
}
