package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;

/**
 * Created by Deimophobe on 5/10/17.
 */
class ChuggingAle extends AbstractAle {
	
	public ChuggingAle(Dwarf dwarf) {
		super(dwarf, 25, 4);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ale.chug", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.heal(12);
		playDefaultHealSound();
	}
}
