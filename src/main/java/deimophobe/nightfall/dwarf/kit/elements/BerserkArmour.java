package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 1/11/17.
 */
class BerserkArmour extends AbstractElement {
	public BerserkArmour(Dwarf dwarf) {
		super(dwarf);
		Armour armour = dwarf.getArmour();
		armour.addModifier(ItemModifierType.ATTACK, 10, "Berserker");
		armour.addModifier(ItemModifierType.SPEED, 10, "Berserker");
		armour.addModifier(ItemModifierType.HEALTH, -4, "Berserker");
		dwarf.getArmour().addModifier(ItemModifierType.DURABILITY, -300, "Berserker Armour");
		armour.increaseMax(-300);
	}
}
