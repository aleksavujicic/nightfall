package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
public abstract class Consumable implements ItemMatcher {
	
	protected static final int DEFAULT_CD = 10;
	protected static final int FAILED_CD = -1;
	
	private final CustomItem item;
	private final ItemStack itemStack;
	
	protected final CustomItem getItem() { return item; }
	public final ItemStack getItemStack() {
		return itemStack;
	}
	public boolean matchesItem(ItemStack toMatch) {
		return item.isSimilar(toMatch);
	}
	
	@Override
	public boolean doesItemMatch(ItemStack item) {
		return matchesItem(item);
	}
	
	protected Consumable(String itemName) {
		this.item = DwarvenItems.getItem("consumable", itemName);
		if (item == null) {
			NightfallPlugin.logger().severe("Consumable item '" + itemName + "' does not exist.");
			throw new NullPointerException("Consumable item '" + itemName + "' does not exist.");
		}
		itemStack = item.createItemStack();
	}
	
	public abstract int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face);
	
	protected boolean checkPhase(Dwarf dwarf) {
		switch (Game.getGame().getPhase()) {
			case BUILD:
			case PLAGUE:
				dwarf.sendTitleMessage(ChatColor.RED + "Monsters are not released!");
				return false;
			case END:
				dwarf.sendTitleMessage(ChatColor.DARK_RED + "The game is over!");
				return false;
		}
		return true;
	}
	
	protected void reset() {}
	
	public static int use(Dwarf dwarf, ItemStack item, ClickType click, Block clickedBlock, BlockFace face) {
		return use(dwarf, ConsumableType.getConsumableType(item), click, clickedBlock, face);
	}
	
	public static int use(Dwarf dwarf, ConsumableType type, ClickType click, Block clickedBlock, BlockFace face) {
		if (type != null)
			return type.getConsumable().use(dwarf, click, clickedBlock, face);
		return FAILED_CD;
	}
}
