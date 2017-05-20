package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;

/**
 * Created by Deimophobe on 28/03/17.
 */
class Runeblessed extends AbstractElement {
	public Runeblessed(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().increaseMax(1000);
	}
}
