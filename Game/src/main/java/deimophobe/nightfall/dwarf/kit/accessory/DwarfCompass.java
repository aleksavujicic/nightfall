package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.map.GameCompass;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 31/03/17.
 */
public class DwarfCompass extends AbstractItem {
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "compass");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getPickupType() {return PickupType.START;}
	
	private final GameCompass compass;
	
	public DwarfCompass(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
		compass = new GameCompass(dwarf);
	}
	
	@Override
	public boolean onUse(ClickType click, Block block, BlockFace face) {
		return compass.tryUse(click);
	}
}
