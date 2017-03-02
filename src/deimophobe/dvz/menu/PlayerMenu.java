package deimophobe.dvz.menu;

import deimophobe.dvz.menu.Menu;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class PlayerMenu extends Menu<Player> {
	public PlayerMenu(String title, int rows) {
		super(title, rows);
	}
	
	@Override
	public void showTo(Player player) {
		player.openInventory(getInventory(player));
	}
}
