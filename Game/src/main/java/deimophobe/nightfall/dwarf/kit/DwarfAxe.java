package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import org.bukkit.Sound;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfAxe extends AbstractItem {
	
	public DwarfAxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("misc", "axe");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() { return PickupType.AXE; }
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		if (NFBlocks.LOG.matchesBlock(block)) {
			dwarf.giveConsumable(ConsumableType.LOG, 2);
			dwarf.playSound(Sound.ITEM_AXE_STRIP, 1f, 0.8f, true);
		}
	}
}
