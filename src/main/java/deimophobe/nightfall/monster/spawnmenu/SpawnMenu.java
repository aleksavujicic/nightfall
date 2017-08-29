package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.menu.*;
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
	
	private final Map<MobType, Set<String>> upgradeSets;
	
	@Override public String getTitle() { return "MonsterEntity Menu"; }
	@Override public MonsterPlayer getDataFromPlayer(Player player) {return MonsterManager.getManager().getGamePlayer(player);}
	@Override protected PageType getDefault() {return PageType.MAIN;}
	
	public SpawnMenu() {
		// Setup front menu
		frontMenu = new SimpleMenu<>(SIZE);
		setPage(PageType.MAIN, frontMenu);
		
		// Setup back/rebirth/doom items
		MenuItem<MonsterPlayer> backItem = new IndexedPageChanger<>(getConfigItem("back"), this, PageType.MAIN);
		MenuItem<MonsterPlayer> zombiePage = new IndexedPageChanger<>(getConfigItem("zombie-page"), this, PageType.ZOMBIE_UPGRADE);
		MenuItem<MonsterPlayer> rebirthItem = new RebirthItem(getConfigItem("rebirth"));
		MenuItem<MonsterPlayer> doomItem = new DoomClockItem(getConfigItem("doomclock"), 250, 15);
		
		// Add items to front menu
		addSpawnEgg(0, "zombie");
		addSpawnEgg(1, "gobo");
		addSpawnEgg(11, "witherskele");
		addSpawnEgg(12, "flamelancer");
		addSpawnEgg(13, "wolf");
		addSpawnEgg(14, "spiderling");
		addSpawnEgg(15, "rat");
		addSpawnEgg(16, "golem");
		addSpawnEgg(24, "minotaur");
		addSpawnEgg(25, "wraith");
		
		frontMenu.setItem(8, doomItem);
		frontMenu.setItem(9, zombiePage);
		frontMenu.setItem(18, rebirthItem);
		
		
		// Setup upgrade pages
		upgradeSets = new HashMap<>();
		
		for (PageType pageType : PageType.values()) {
			String file = pageType.filename;
			MobType type = pageType.type;
			if (file != null) {
				UpgradeMenu upgradeMenu = new UpgradeMenu(Misc.getInternalFileConfig(file), type);
				upgradeMenu.setItem(0, SpawnEggMenuItem.getEgg(type));
				upgradeMenu.setItem(18, backItem);
				if (pageType == PageType.ZOMBIE_UPGRADE)
					upgradeMenu.setItem(9, rebirthItem);
				
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
	
	
	
	public Set<String> getUpgradeSet(MobType type) {
		if (upgradeSets.containsKey(type)) {
			return upgradeSets.get(type);
		} else {
			throw new IllegalArgumentException("Mob type: " + type + " has no upgrade page.");
		}
	}
	
	// This is a bit hacky but does the job p well.
	enum PageType {
		MAIN,
		ZOMBIE_UPGRADE("zombie-upgrades.yml", MobType.ZOMBIE),
		GOBO_UPGRADE
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
