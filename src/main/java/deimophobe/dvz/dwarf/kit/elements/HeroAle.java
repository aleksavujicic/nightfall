package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 10/03/17.
 */
class HeroAle extends AbstractElement {
	public HeroAle(Dwarf dwarf) { super(dwarf); }
	
	@Override
	public void onLateGotHit(GameEntity monster, DamageType type, double damage) {
		double health = dwarf.getPlayer().getHealth();
		if (health - damage <= 0.1 || health <= 16) {
			if (dwarf.tryUseMana(100)) {
				dwarf.healMax();
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, true);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			}
		}
	}
}
