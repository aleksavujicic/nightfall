package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 27/03/17.
 */
class StuddedArmour extends AbstractElement{
	
	StuddedArmour(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.SPEED, 20, "Studded Runeleather");
	}
}
