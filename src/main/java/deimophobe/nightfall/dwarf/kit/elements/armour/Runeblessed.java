package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;

/**
 * Created by Deimophobe on 28/03/17.
 */
public class Runeblessed extends AbstractElement {
	public Runeblessed(Dwarf dwarf) {
		super(dwarf);
		Armour armour = dwarf.getArmour();
		if (armour instanceof DwarvenArmour)
			((DwarvenArmour) armour).changeDurability(100, "Runeblessed");
	}
}
