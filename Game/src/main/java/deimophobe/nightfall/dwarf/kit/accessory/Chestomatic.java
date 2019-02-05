package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 21/06/17.
 */
public class Chestomatic extends AbstractItem {
	
	public Chestomatic(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "chesto");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public PickupType getPickupType() { return PickupType.START; }
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block block, BlockFace face) {
		dwarf.showSharedChest();
		return true;
	}
}
