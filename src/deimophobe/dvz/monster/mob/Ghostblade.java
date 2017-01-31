package deimophobe.dvz.monster.mob;

import deimophobe.dvz.GameListener;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 31/01/17.
 */
class Ghostblade extends Mob {
	private int cooldown = 0;
	private final static int MAX_CD = 100;
	
	protected Ghostblade(MonsterPlayer mons, MobType type) {
		super(mons, type);
	}
	
	@Override
	public void update() {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float)cooldown /MAX_CD;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock) {
		if (DwarvenItem.isRightClick(action)) {
			if (cooldown == 0) {
				Dwarf dwarf = monster.getLookingAt(2, 10);
				if (dwarf != null) {
					Location dwarfLoc = dwarf.getLocation();
					Vector lookDir = dwarfLoc.getDirection();
					monster.teleportTo(dwarfLoc.subtract(lookDir));
					monster.playSound("entity.endermen.teleport", 1, 1, true);
					cooldown = MAX_CD;
				}
			}
		}
	}
}
