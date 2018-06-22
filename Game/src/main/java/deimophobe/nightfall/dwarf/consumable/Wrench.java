package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Wrench extends Consumable {
	
	private static final ConsumeResult ARMOUR_FULL = ConsumeResult.failedResultWithMessage(ChatColor.GOLD + "Your armour is nearly full!");
	
	Wrench(String item) {
		super(item);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return ConsumeResult.FAILURE;
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		
		Armour armour = dwarf.getArmour();
		boolean canUse = (armour.canPickRepair() || !armour.isArmoured());
	
		if (canUse) {
			boolean success = GameMap.getCurrentMap().useGold(75);
			armour.repair(success ? 1000 : 250);
			dwarf.playSound("block.anvil.use", 20, 0.8f, false);
			
			if (!armour.isArmoured() && armour instanceof DwarvenArmour) {
				((DwarvenArmour) armour).putOn();
			}
			return ConsumeResult.SUCCESS;
		} else {
			return ARMOUR_FULL;
		}
	}
}
