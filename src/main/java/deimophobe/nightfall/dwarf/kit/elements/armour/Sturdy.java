package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class Sturdy extends AbstractElement {
	public Sturdy(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, 4, "Sturdy Armour");
	}
}
