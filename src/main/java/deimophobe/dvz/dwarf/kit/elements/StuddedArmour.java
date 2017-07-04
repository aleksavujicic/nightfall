package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 27/03/17.
 */
class StuddedArmour extends AbstractElement{
	
	StuddedArmour(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.SPEED, 20, "Studded Runeleather");
	}
}
