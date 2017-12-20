package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class StuddedArmour extends AbstractElement {
	
	public StuddedArmour(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.SPEED, 20, "Studded Runeleather");
	}
}
