package deimophobe.nightfall.lobby.game;

import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import deimophobe.nightfall.lobby.game.menu.GameMenu;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

import java.util.Collection;

/**
 * Created by Deimophobe on 19/12/17.
 */
public class GameListener implements Listener {
	
	@EventHandler
	public void portalJoinEvent(EntityPortalEnterEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			
			boolean gameMenuOpen = MenuManager.getManager().hasOpenSession(GameMenu.class, player);
			if (gameMenuOpen) return;
			
			Collection<Game> runningGames = GameManager.getManager().getRunningGames();
			if (runningGames.isEmpty()) {
				NightfallLobbyPlugin.getPlugin().resetPlayer(player, true);
			} else {
				player.teleport(player.getLocation().subtract(0,300,0));
				GameManager.getManager().openMenu(player);
			}
		}
	}
}
