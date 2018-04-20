package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;

/**
 * Created by Deimophobe on 28/03/17.
 */
public class Quiver extends AbstractPiece {
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
