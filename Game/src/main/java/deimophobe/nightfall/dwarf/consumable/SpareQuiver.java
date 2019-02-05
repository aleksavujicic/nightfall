package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 11/05/18.
 */
public class SpareQuiver extends Consumable {
	protected SpareQuiver(String itemName) {
		super(itemName);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face) {
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		if (!click.isLeftClick()) return ConsumeResult.FAILURE;
		
		dwarf.restockArrows();
		
		return ConsumeResult.SUCCESS;
	}
}
