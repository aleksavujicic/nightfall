package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.mob.MobType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
class SelectUpgradeMenuItem extends MobMenuItem {
	private final MobType type;
	SelectUpgradeMenuItem(ItemStack item, MobType type) {
		super(item, 0);
		this.type = type;
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	boolean onSelect(MonsterPlayer monster) {
		monster.showUpgradeMenu(type);
		return false;
	}
}
