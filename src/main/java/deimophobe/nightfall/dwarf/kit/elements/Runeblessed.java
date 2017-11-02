package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 28/03/17.
 */
class Runeblessed extends AbstractElement {
	public Runeblessed(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().increaseMax(1000);
		dwarf.getArmour().addModifier(ItemModifierType.DURABILITY, 1000, "Runeblessed Armour");
	}
}
