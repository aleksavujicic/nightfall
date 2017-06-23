package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;

/**
 * Created by Deimophobe on 27/03/17.
 */
class Safefall extends AbstractElement {
	
	public Safefall(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		if (type == DamageType.FALL) {
			damage *= 0.1;
		}
		return damage;
	}
}
