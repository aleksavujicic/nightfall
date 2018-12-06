package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;

/**
 * Created by Deimophobe on 28/03/17.
 */
public class Quiver extends AbstractPiece implements ArmourPiece {
	public Quiver(Dwarf dwarf) {
		super(dwarf);
		dwarf.setMaxArrows(30);
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.QUIVER, 10, "Massive Quiver");
	}
	
	@Override
	public void onKill(MonsterDamage damage) {
		dwarf.giveArrows(1);
	}
}
