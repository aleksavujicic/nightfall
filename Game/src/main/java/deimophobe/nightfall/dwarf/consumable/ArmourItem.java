package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.function.Predicate;

/**
 * Created by Deimophobe on 23/01/17.
 */
class ArmourItem extends Consumable {
	ArmourItem(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isLeftClick()) {
			Predicate<Dwarf> armourChecker = checkee -> {
				Armour armour = checkee.getArmour();
				return (armour instanceof DwarvenArmour && !armour.isArmoured());
			};
			
			Dwarf toArmour = dwarf.getLookingAt(7, 2, DwarfManager.getManager().getDwarves(), armourChecker);
			if (toArmour != null) {
				((DwarvenArmour) toArmour.getArmour()).putOn();
				return DEFAULT_CD;
			}
		}
		return FAILED_CD;
	}
}
