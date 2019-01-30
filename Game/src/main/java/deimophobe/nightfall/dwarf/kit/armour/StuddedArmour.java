package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class StuddedArmour extends AbstractPiece implements ArmourPiece {
	
	public StuddedArmour(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.SPEED, 20, "Studded Armour");
	}
}
