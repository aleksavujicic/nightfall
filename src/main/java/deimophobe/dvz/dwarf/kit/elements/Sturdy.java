package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 20/05/17.
 */
class Sturdy extends AbstractElement {
	Sturdy(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, 5, "Sturdy Armour");
	}
}
