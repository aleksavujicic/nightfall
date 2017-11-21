package deimophobe.nightfall.dwarf.kit.elements.healing;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class HeroAle extends AbstractElement {
	public HeroAle(Dwarf dwarf) { super(dwarf); }
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		double health = dwarf.getPlayer().getHealth();
		if (damage.willKill() || health <= 10) {
			if (dwarf.tryUseMana(100)) {
				dwarf.healMax();
				dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, true);
				dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
				
				if (damage.willKill()) {
					damage.getDamage().setMultiplier(1);
					damage.getDamage().setBoost(0);
					damage.getDamage().setBase(dwarf.getMaxHealth() - 2);
				}
			}
		}
	}
}
