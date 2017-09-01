package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.dwarf.Dwarf;

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
