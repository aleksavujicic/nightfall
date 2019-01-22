package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.submenu.CompositeMenu;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 15/01/19.
 */
public class MonsterMenu extends CompositeMenu<MonsterPlayer> implements MainMenu<MonsterPlayer> {
	@Override public String getTitle() { return ChatColor.DARK_RED + "Monster Menu"; }
	@Override public MonsterPlayer getDataFromPlayer(Player player) {return MonsterManager.getManager().getGamePlayer(player);}
	@Override public String getPermissionName() {
		return "spawn";
	}
	
	public MonsterMenu() {
		MonsterMenuConfig monsterMenuConfig = new MonsterMenuConfig(NightfallPlugin.getPlugin());
		
		SpawnerMenu spawnerMenu = new SpawnerMenu();
		UpgradeContainerMenu upgradeContainerMenu = new UpgradeContainerMenu(27, monsterMenuConfig);
		
		this.addSubMenus(upgradeContainerMenu, spawnerMenu);
	}
}
