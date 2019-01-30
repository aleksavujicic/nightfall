package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class Sturdy extends AbstractPiece implements ArmourPiece {
	public Sturdy(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.HEALTH, 4, "Sturdy");
		
		if (armour instanceof DwarvenArmour) {
			((DwarvenArmour) armour).changeDurability(40, "Sturdy");
		}
	}
}
