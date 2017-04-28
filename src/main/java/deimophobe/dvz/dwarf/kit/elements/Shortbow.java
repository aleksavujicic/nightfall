package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 31/03/17.
 */
class Shortbow extends AbstractBow {
	
	public Shortbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 30;
	private final static CustomItem ITEM = DwarvenItems.getBow("shortbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "SHORTBOW";}
	@Override public int getPower() {return POWER;}
}
