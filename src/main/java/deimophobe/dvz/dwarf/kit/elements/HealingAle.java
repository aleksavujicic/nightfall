package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HealingAle extends AbstractAle {
	
	HealingAle(Dwarf dwarf) {
		super(dwarf, 100);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ale.healing", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
}
