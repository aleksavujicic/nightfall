package deimophobe.nightfall.lobby.game.menu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import deimophobe.nightfall.lobby.game.Game;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * Created by Deimophobe on 10/03/18.
 */
public class GameItem extends SimpleItem<GameSessionData> {
	private final Game game;
	public GameItem(Game game) {
		super(getItemFromGame(game));
		this.game = game;
	}
	
	@Override
	public boolean onClick(MenuSession<GameSessionData> session) {
		game.connect(session.getPlayer());
		session.closeSession();
		return false;
	}
	
	
	private static ItemStack getItemFromGame(Game game) {
		ItemStack item = new ItemStack(Material.STONE);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(game.getDisplayName());
		meta.setLore(Collections.singletonList("Players: " + game.getDisplayInt()));
		
		item.setItemMeta(meta);
		return item;
	}
}
