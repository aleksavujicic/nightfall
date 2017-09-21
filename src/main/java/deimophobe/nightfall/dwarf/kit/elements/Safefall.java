package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 27/03/17.
 */
class Safefall extends AbstractElement {
	
	public Safefall(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == NaturalDamageType.FALL)
			damage.getDamage().timesMult(0.1);
	}
}
