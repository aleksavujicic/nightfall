package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/01/17.
 */
public abstract class DwarvenItem {
	protected final Dwarf dwarf;
	protected final ItemStack item;
	
	public ItemStack getItem() {
		return item;
	}
	
	protected DwarvenItem(Dwarf dwarf, ItemStack item) {
		this.dwarf = dwarf;
		this.item = item;
	}
	
	public boolean matchesItem(ItemStack toMatch) {
		return item.isSimilar(toMatch);
	}
	
	public boolean use(Action type) {
		if (canUse(type)) {
			boolean success = ability(type);
			return success;
		} else {
			return false;
		}
	}
	
	public abstract void update();
	public abstract float fractionComplete();
	protected abstract boolean ability(Action type);
	
	protected boolean canUse(Action type) {
		return true;
	}
	
	public static boolean isLeftClick(Action type) {
		return (type == Action.LEFT_CLICK_AIR || type == Action.LEFT_CLICK_BLOCK || type == Action.PHYSICAL);
	}
	public static boolean isRightClick(Action type) {
		return (type == Action.RIGHT_CLICK_AIR || type == Action.RIGHT_CLICK_BLOCK);
	}
}
