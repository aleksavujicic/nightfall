package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.doom.DoomManager;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
class DoomClockItem extends CostMobMenuItem {
	
	private final int time;
	
	DoomClockItem(ItemStack item, int xpCost, int time) {
		super(item, xpCost);
		this.time = time;
	}
	
	@Override
	protected boolean onPayCost(MonsterPlayer monster) {
		DoomManager.getManager().reduceDoom(time);
		return false;
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer monster) {
		return (!DoomManager.getManager().isDoom());
	}
}
