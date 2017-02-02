package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.doom.DoomManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
class DoomClockItem extends MenuItem{
	
	private final int time;
	
	DoomClockItem(ItemStack item, int xpCost, int time) {
		super(item, xpCost);
		this.time = time;
	}
	
	@Override
	boolean onSelect(MonsterPlayer monster) {
		DoomManager.getManager().reduceDoom(time);
		return false;
	}
	
	@Override
	boolean isAvailable() {
		return (Game.getGame().getPhase() != Phase.DOOM);
	}
}
