package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.ColourMenu;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class GlassConsumable extends Consumable {
	
	public GlassConsumable(String name) {
		super(name);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isLeftClick() && !BlockType.FURNACE.matchesBlock(clickedBlock)) {
			ColourMenu.getMenu().startSession(dwarf.getPlayer());
		}
		return FAILED_CD;
	}
	
	@Override
	public boolean matchesItem(ItemStack toMatch) {
		if (toMatch == null) return false;
		return super.matchesItem(toMatch) || toMatch.getType() == Material.STAINED_GLASS;
	}
}
