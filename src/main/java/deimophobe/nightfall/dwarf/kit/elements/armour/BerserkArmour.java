package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class BerserkArmour extends AbstractElement {
	public BerserkArmour(Dwarf dwarf) {
		super(dwarf);
		Armour armour = dwarf.getArmour();
		armour.addModifier(ItemModifierType.ATTACK, 5, "Berserker");
		armour.addModifier(ItemModifierType.SPEED, 10, "Berserker");
		armour.addModifier(ItemModifierType.HEALTH, -3, "Berserker");
		
		if (armour instanceof DwarvenArmour)
			((DwarvenArmour) armour).changeDurability(-30, "Berserker");
	}
	
	
}
