package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class Sturdy extends AbstractPiece {
	public Sturdy(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, 4, "Sturdy");
		
		Armour armour = dwarf.getArmour();
		if (armour instanceof DwarvenArmour)
			((DwarvenArmour) armour).changeDurability(40, "Sturdy");
	}
}
