package deimophobe.nightfall.lobby.game.menu;

import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.submenu.ListMenu;
import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import deimophobe.nightfall.lobby.game.Game;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 10/03/18.
 */
public class GameMenu extends ListMenu<GameSessionData> implements MainMenu<GameSessionData> {
	@Override public String getTitle() { return "Games"; }
	@Override public GameSessionData getDataFromPlayer(Player player) { return new GameSessionData(player); }
	@Override public String getMenuPermission() {
		return "games";
	}
	
	private final Map<Integer, GameItem> menuItems = new HashMap<>();
	
	
	public void startGame(Game game) {
		GameItem item = new GameItem(game);
		addItem(item);
		menuItems.put(game.getId(), item);
	}
	
	public void stopGame(Game game) {
		GameItem item = menuItems.remove(game.getId());
		removeItem(item);
	}
	
	@Override
	public void onClose(MenuSession<GameSessionData> session) {
		super.onClose(session);
		NightfallLobbyPlugin.getPlugin().resetPlayer(session.getPlayer(), true);
	}
}
