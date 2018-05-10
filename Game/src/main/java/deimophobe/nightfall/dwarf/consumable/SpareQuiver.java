package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 11/05/18.
 */
public class SpareQuiver extends Consumable {
	protected SpareQuiver(String itemName) {
		super(itemName);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		if (!click.isLeftClick()) return FAILED_CD;
		
		dwarf.restockArrows();
		
		return DEFAULT_CD;
	}
}
