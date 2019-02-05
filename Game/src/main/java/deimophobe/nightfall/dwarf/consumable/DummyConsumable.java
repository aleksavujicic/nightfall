package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DummyConsumable extends Consumable {
	protected DummyConsumable(String item) {
		super(item);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face) {
		return ConsumeResult.FAILURE;
	}
}
