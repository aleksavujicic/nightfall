package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.*;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class SpawnMenu extends IndexedMenu<MonsterPlayer, SpawnMenu.PageType> implements MainMenu<MonsterPlayer> {
	
	private static final int SIZE = 27;
	private final SimpleMenu<MonsterPlayer> frontMenu;
	private final SimpleMenu<MonsterPlayer> resetMenu;
	private final Map<MobType, Set<String>> upgradeSets;

	@Override public String getTitle() { return "Monster Menu"; }
	@Override public MonsterPlayer getDataFromPlayer(Player player) {return MonsterManager.getManager().getGamePlayer(player);}
	@Override protected PageType getDefault() {return PageType.MAIN;}
	
	public SpawnMenu() {
		// Setup front menu
		frontMenu = new SimpleMenu<>(SIZE);
		MenuItem<MonsterPlayer> backItem = new IndexedPageChanger<>(getConfigItem("back"), this, PageType.MAIN);
		setPage(PageType.MAIN, frontMenu);
		upgradeSets = new HashMap<>();

		// Setup back/rebirth/doom items
		MenuItem<MonsterPlayer> zombiePage = new IndexedPageChanger<>(getConfigItem("zombie-page"), this, PageType.ZOMBIE_UPGRADE);
		MenuItem<MonsterPlayer> skeletonPage = new IndexedPageChanger<>(getConfigItem("skeleton-page"), this, PageType.SKELETON_UPGRADE);
		MenuItem<MonsterPlayer> goboPage = new IndexedPageChanger<>(getConfigItem("gobo-page"), this, PageType.GOBO_UPGRADE);
		MenuItem<MonsterPlayer> rebirthItem = new RebirthItem(getConfigItem("rebirth"));
		MenuItem<MonsterPlayer> resetItem = new ResetItem(getConfigItem("reset-page"), this, PageType.MAIN);
		//MenuItem<MonsterPlayer> doomItem = new DoomClockItem(getConfigItem("doomclock"), 250, 15);

		// Setup reset menu
		resetMenu = new SimpleMenu<>(SIZE);
		MenuItem<MonsterPlayer> resetPage = new IndexedPageChanger<>(getConfigItem("reset-page"), this, PageType.RESETXP);
		MenuItem<MonsterPlayer> cancelItem = new IndexedPageChanger<>(getConfigItem("cancel"), this, PageType.MAIN);
		setPage(PageType.RESETXP, resetMenu);
		resetMenu.setItem(11, resetItem);
		resetMenu.setItem(15, cancelItem);

		// Add items to front menu
		addSpawnEgg(0, "zombie");
		addSpawnEgg(1, "skeleton");
		addSpawnEgg(2, "gobo");
		addSpawnEgg(13, "ember_sprite");
		addSpawnEgg(14, "wolf");
		addSpawnEgg(15, "spiderling");
		addSpawnEgg(16, "rat");
		addSpawnEgg(24, "golem");
		addSpawnEgg(25, "minotaur");
		addSpawnEgg(26, "wraith");
		
		frontMenu.setItem(8, resetPage);
		frontMenu.setItem(9, zombiePage);
		frontMenu.setItem(10, skeletonPage);
		frontMenu.setItem(11, goboPage);
		frontMenu.setItem(18, rebirthItem);

		for (SpawnMenu.PageType pageType : SpawnMenu.PageType.values()) {
			String file = pageType.filename;
			MobType type = pageType.type;
			if (file != null) {
				UpgradeMenu upgradeMenu = new UpgradeMenu(Misc.getInternalFileConfig(file), type);
				upgradeMenu.setItem(0, SpawnEggMenuItem.getEgg(type));
				if (type == MobType.ZOMBIE) {
					upgradeMenu.setItem(9, rebirthItem);
				}
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
	
	public void addSpawnEgg(int index, String name) {
		frontMenu.setItem(index, SpawnEggMenuItem.getEgg(name));
	}
	
	public void updateEggs() {
		for (MenuItem item : frontMenu.getMenuItems()) {
			if (item instanceof SpawnEggMenuItem)
				((SpawnEggMenuItem)item).tryRestock();
		}
	}
	
	public void doomRestockAllEggs() {
		// A shitty hack so that not all eggs proc but some do
		for (MenuItem item : frontMenu.getMenuItems()) {
			if (item instanceof SpawnEggMenuItem) {
				for (int i=0; i<10; i++)
					((SpawnEggMenuItem) item).tryRestock();
			}
		}
	}

	public Set<String> getUpgradeSet(MobType type) {
		if (upgradeSets.containsKey(type)) {
			return upgradeSets.get(type);
		} else {
			throw new IllegalArgumentException("Mob type: " + type + " has no upgrade page.");
		}
	}

	enum PageType {
		MAIN,
		ZOMBIE_UPGRADE("zombie-upgrades.yml", MobType.ZOMBIE),
        SKELETON_UPGRADE("skeleton-upgrades.yml", MobType.SKELETON),
		GOBO_UPGRADE("gobo-upgrades.yml", MobType.GOBO),
		RESETXP;

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
