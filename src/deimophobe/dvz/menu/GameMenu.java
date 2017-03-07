package deimophobe.dvz.menu;

import deimophobe.dvz.GamePlayer;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class GameMenu<T extends GamePlayer> extends SinglePageMenu<T> {
	public GameMenu(String title, int rows) {
		super(title, rows);
	}
	
	@Override
	public void showTo(T player) {
		player.showInventory(getInventory(player));
	}
}
