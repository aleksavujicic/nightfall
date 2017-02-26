package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class SOS extends Consumable {
	
	SOS(ItemStack item) {
		super(item);
	}
	
	private static final double RANGE = 30;
	private static final int FREEZE_TIME = 200;
	
	@Override
	public boolean use(Dwarf dwarf) {
		Location center = dwarf.getEyeLocation();
		for (MonsterPlayer mp : MonsterManager.getManager().getGamePlayers()) {
			if (center.distance(mp.getLocation()) <= RANGE)
				mp.freeze(FREEZE_TIME);
		}
		AIManager.getManager().clearArea(center, RANGE);
		dwarf.playSound("entity.evocation_illager.prepare_summon", 10, 1f, true);
		return true;
	}
}
