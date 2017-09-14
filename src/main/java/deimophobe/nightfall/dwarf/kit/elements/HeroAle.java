package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 10/03/17.
 */
class HeroAle extends AbstractElement {
	public HeroAle(Dwarf dwarf) { super(dwarf); }
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		double health = dwarf.getPlayer().getHealth();
		if (damage.willKill() || health <= 16) {
			if (dwarf.tryUseMana(100)) {
				dwarf.healMax();
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, true);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
			}
		}
	}
}
