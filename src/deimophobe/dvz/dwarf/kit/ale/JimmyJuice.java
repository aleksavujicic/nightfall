package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.PlayerOrAI;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 22/01/17.
 */
class JimmyJuice extends Ale {
	JimmyJuice(Dwarf dwarf) {
		super(dwarf, AleType.JIMMYJUICE, 150);
	}
	
	@Override
	public void onGotHit(PlayerOrAI monster, DamageType type, double damage) {
		double health = dwarf.getPlayer().getHealth();
		if (health - damage <= 0.1 || health <= 16) {
			if (useMana()) {
				dwarf.healPlayerMax();
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			}
		}
	}
	
	@Override
	protected boolean ability(Action type) {
		return false;
	}
}
