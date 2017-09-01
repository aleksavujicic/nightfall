package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.entity.GameEntity;
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
	public void onKill(GameEntity monster, GameDamageType type) {
		dwarf.giveArrows(2);
	}
}
