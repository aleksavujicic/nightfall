package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 22/01/17.
 */
public abstract class Consumable implements ItemMatcher {
	
	private final CustomItem item;
	private final ItemStack itemStack;
	
	protected Consumable(String name) {
		this.item = DwarvenItems.getItem("consumable", name);
		if (item == null) throw new IllegalArgumentException("Consumable item '" + name + "' does not exist.");
		
		this.itemStack = item.createItemStack();
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack toMatch) {
		return item.isSimilar(toMatch);
	}
	
	final ItemStack getItemStack() {
		return itemStack;
	}
	
	public abstract ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face);
	
	
	
	protected static ConsumeResult checkPhase() {
		switch (Game.getGame().getPhase()) {
			case BUILD:
			case PLAGUE: return ConsumeResult.failedResultWithMessage("Monsters are not released!");
			
			case END:    return ConsumeResult.failedResultWithMessage(ChatColor.DARK_RED + "The game is over!");
		}
		return null;
	}
}
