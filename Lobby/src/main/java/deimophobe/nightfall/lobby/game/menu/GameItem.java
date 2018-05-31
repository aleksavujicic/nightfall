package deimophobe.nightfall.lobby.game.menu;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import deimophobe.nightfall.lobby.game.Game;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 10/03/18.
 */
public class GameItem implements MenuItem<GameSessionData> {
	private static final CustomItem TEMPLATE = createTemplate();
	private static CustomItem createTemplate() {
		YamlConfiguration gameMenuConfig = NightfallLobbyPlugin.getInternalFileConfig("game-menu.yml");
		ConfigurationSection gameSection = gameMenuConfig.getConfigurationSection("game");
		
		return CustomItem.getItem(gameSection, "game-menu");
	}
	
	private final Game game;
	private ItemStack menuItem;
	
	GameItem(Game game) {
		this.game = game;
		regenerateItem();
	}
	
	private void regenerateItem() {
		CustomItem menuTemplate = TEMPLATE.clone();
		menuTemplate.setName(game.getDisplayName());
		menuTemplate.applyVariable("player-count", "" +game.getPlayerCount());
		menuTemplate.applyVariable("map", game.getMapName());
		
		String status = "Not implemented yet.";
		menuTemplate.applyVariable("status", status);
		
		menuTemplate.applyVariable("gamemodes", "no modes");
		menuItem = menuTemplate.createItemStack();
	}
	
	@Override
	public boolean onClick(MenuSession<GameSessionData> session) {
		game.connect(session.getPlayer());
		session.closeSession();
		return false;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<GameSessionData> session) {
		return menuItem;
	}
}
