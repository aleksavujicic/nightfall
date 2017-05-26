package deimophobe.dvz.dwarf.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DummyConsumable extends Consumable {
	protected DummyConsumable(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		return FAILED_CD;
	}
}
