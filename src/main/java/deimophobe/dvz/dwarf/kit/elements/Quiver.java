package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;

/**
 * Created by Deimophobe on 28/03/17.
 */
class Quiver extends AbstractElement {
	public Quiver(Dwarf dwarf) {
		super(dwarf);
		dwarf.setMaxArrows(40);
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		dwarf.giveArrow();
	}
}
