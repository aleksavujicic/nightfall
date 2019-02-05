package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.ColourMenu;
import deimophobe.nightfall.blocks.NFBlocks;
import deimophobe.nightfall.blocks.blocktype.RepeatMaterial;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 4/05/18.
 */
class GlassConsumable extends Consumable {
	private static final ConsumeResult OPENED_MENU = new ConsumeResult(null, false, 20);
	private static final ConsumeResult CLICKED_GLASS = new ConsumeResult(null, false, 20);
	private static final ItemMatcher GLASS_MATCHER = RepeatMaterial.colourMaterial("stained-glass");
	
	GlassConsumable(String name) {
		super(name);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face) {
		if (!click.isLeftClick()) return ConsumeResult.FAILURE;
		if (clickedBlock != null && (NFBlocks.FURNACE.matchesBlock(clickedBlock) || NFBlocks.GLASS.matchesBlock(clickedBlock))) {
			return CLICKED_GLASS;
		}
		
		MenuManager.getManager().startSession(ColourMenu.class, dwarf.getPlayer());
		return OPENED_MENU;
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack toMatch) {
		return super.doesItemMatch(toMatch) || GLASS_MATCHER.doesItemMatch(toMatch);
	}
}
