package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 22/01/17.
 */
class JimmyJuice extends Ale {
	JimmyJuice(Dwarf dwarf) {
		super(dwarf, AleType.JIMMYJUICE, 120);
	}
	
	@Override
	public void onGotHit(GameEntity monster, DamageType type, double damage) {
		double health = dwarf.getPlayer().getHealth();
		if (health - damage <= 0.1 || health <= 16) {
			if (useMana()) {
				dwarf.healMax();
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			}
		}
	}
	
	@Override
	protected boolean ability(Action type) {
		if (dwarf.useMana(100)) {
			dwarf.healMax();
			dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
			dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			return true;
		}
		return false;
	}
}
