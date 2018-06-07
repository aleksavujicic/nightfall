package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 7/06/18.
 */
class Bandage extends Consumable {
	private static final ConsumeResult FULL_MANA = ConsumeResult.failedResultWithMessage("Your mana is full");
	
	protected Bandage(String itemName) {
		super(itemName);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!click.isLeftClick()) return ConsumeResult.FAILURE;
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		
		if (dwarf.getMana() == 1000) return FULL_MANA;
		dwarf.regenMana(200);
		dwarf.playSound("healing");
		
		return ConsumeResult.SUCCESS;
	}
}
