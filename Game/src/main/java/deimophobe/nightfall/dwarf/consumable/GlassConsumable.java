package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.ColourMenu;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 4/05/18.
 */
class GlassConsumable extends Consumable {
	private static final ConsumeResult OPENED_MENU = new ConsumeResult(null, false, 20);
	
	GlassConsumable(String name) {
		super(name);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isLeftClick() && !BlockType.FURNACE.matchesBlock(clickedBlock) && !BlockType.GLASS.matchesBlock(clickedBlock)) {
			MenuManager.getManager().startSession(ColourMenu.class, dwarf.getPlayer());
			return OPENED_MENU;
		}
		return ConsumeResult.FAILURE;
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack toMatch) {
		return super.doesItemMatch(toMatch) || toMatch.getType() == Material.STAINED_GLASS;
	}
}
