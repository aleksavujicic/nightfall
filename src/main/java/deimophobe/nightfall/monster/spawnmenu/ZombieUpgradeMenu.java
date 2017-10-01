package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.menu.*;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by TKiwisi on 9/21/17.
 */
class ZombieUpgradeMenu extends IndexedMenu<MonsterPlayer, ZombieUpgradeMenu.PageType> {
	private final Map<MobType, Set<String>> upgradeSets;

	@Override protected ZombieUpgradeMenu.PageType getDefault() {return ZombieUpgradeMenu.PageType.ZOMBIE_UPGRADE;}

	//private ZombieUpgradeMenu.PageType getHusk() {return ZombieUpgradeMenu.PageType.ZOMBIE_HUSK_UPGRADE;}

	public ZombieUpgradeMenu(MenuItem<MonsterPlayer> backItem) {
		upgradeSets = new HashMap<>();

		MenuItem<MonsterPlayer> rebirthItem = new RebirthItem(getConfigItem("rebirth"));

		for (ZombieUpgradeMenu.PageType pageType : ZombieUpgradeMenu.PageType.values()) {
			String file = pageType.filename;
			MobType type = pageType.type;
			if (file != null) {
				UpgradeMenu upgradeMenu = new UpgradeMenu(Misc.getInternalFileConfig(file), type);
				upgradeMenu.setItem(0, SpawnEggMenuItem.getEgg(type));
				upgradeMenu.setItem(9, rebirthItem);
				upgradeMenu.setItem(18, backItem);

				setPage(pageType, upgradeMenu);
				upgradeSets.put(type, upgradeMenu.getUpgrades());
			}
		}
	}
	private static final YamlConfiguration itemConfig = Misc.getInternalFileConfig("mobmenu-items.yml");
	private ItemStack getConfigItem(String name) {
		return CustomItem.getItem(itemConfig.getConfigurationSection(name), "monster-menu", Slot.MAIN_HAND).createItemStack();
	}

	@Override
	protected ZombieUpgradeMenu.PageType getPageIndex(MenuSession<MonsterPlayer> session) {
		/*
		if (session.getData().getUpgrades(MobType.ZOMBIE).computeIfAbsent("husk", (k) -> 0) == 1) {
			return storedIndices.computeIfAbsent(session, (k) -> getHusk());
		}
		*/
		return storedIndices.computeIfAbsent(session, (k) -> getDefault());
	}

	public Set<String> getUpgradeSet(MobType type) {
		if (upgradeSets.containsKey(type)) {
			return upgradeSets.get(type);
		} else {
			throw new IllegalArgumentException("Mob type: " + type + " has no upgrade page.");
		}
	}


	enum PageType {
		ZOMBIE_UPGRADE("zombie-upgrades.yml", MobType.ZOMBIE),
		//ZOMBIE_HUSK_UPGRADE("husk-upgrades.yml", MobType.ZOMBIE_HUSK)//,
		//ZOMBIE_SABOTEUR_UPGRADE,
		//ZOMBIE_FURY_UPGRADE
		;

		private final String filename;
		private final MobType type;

		PageType() {
			filename = null;
			type = null;
		}

		PageType(String filename, MobType type) {
			this.filename = filename;
			this.type = type;
		}
	}
}