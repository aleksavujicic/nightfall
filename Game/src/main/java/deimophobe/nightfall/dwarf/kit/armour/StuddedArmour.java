package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class StuddedArmour extends AbstractPiece {
	
	public StuddedArmour(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.SPEED, 20, "Studded Armour");
	}
}
