package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 28/03/17.
 */
class Quiver extends AbstractElement {
	public Quiver(Dwarf dwarf) {
		super(dwarf);
		dwarf.setMaxArrows(40);
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		dwarf.giveArrows(2);
	}
}
