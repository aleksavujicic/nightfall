package deimophobe.dvz.menu;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;

import java.util.Set;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class GameMenu<T extends GamePlayer> extends Menu<T> {
	public GameMenu(String title, int rows) {
		super(title, rows);
	}
	
	public void showTo(T player) {
		player.showInventory(getInventory(player));
	}
	
	@Override
	public boolean select(int i, T player) {
		boolean refresh = super.select(i, player);
		
		if (refresh)
			showTo(player);
		
		return false;
	}
}
