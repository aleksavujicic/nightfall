package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;

/**
 * Created by Deimophobe on 28/03/17.
 */
public class Quiver extends AbstractElement {
	public Quiver(Dwarf dwarf) {
		super(dwarf);
		dwarf.setMaxArrows(30);
		dwarf.getArmour().addModifier(ItemModifierType.QUIVER, 10, "Massive Quiver");
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		dwarf.giveArrows(1);
	}
}
