package deimophobe.nightfall.common.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.loadout.LoadoutMenu;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.common.player.cosmetic.HatMenu;
import deimophobe.nightfall.common.player.cosmetic.TitleMenu;
import deimophobe.nightfall.common.player.settings.SettingsMenu;
import org.bukkit.entity.Player;


/**
 * Created by Deimophobe on 14/05/18.
 */
public class MenuCommands extends BaseCommand {
	
	@CommandAlias("hat|hats")
	@CommandPermission("nightfall.menu.hat")
	public void hat(Player player) {
		startSession(HatMenu.class, player);
	}
	
	@CommandAlias("title|titles")
	@CommandPermission("nightfall.menu.title")
	public void title(Player player) {
		startSession(TitleMenu.class, player);
	}
	
	@CommandAlias("loadout|kit")
	@CommandPermission("nightfall.menu.loadout")
	public void loadout(Player player) {
		startSession(LoadoutMenu.class, player);
	}
	
	@CommandAlias("settings")
	@CommandPermission("nightfall.menu.settings")
	public void settings(Player player) {
		startSession(SettingsMenu.class, player);
	}
	
	private <T extends SessionData> void startSession(Class<? extends MainMenu<T>> menuClass, Player player) {
		MenuManager menuManager = MenuManager.getManager();
		if (!menuManager.hasOpenSession(player)) {
			menuManager.startSession(menuClass, player);
		} else {
			NightfallCommonPlugin.logger().warning("Tried to open menu '" + menuClass.getSimpleName() + "' for player '" + player + "' but menu is already open.");
		}
	}
}
