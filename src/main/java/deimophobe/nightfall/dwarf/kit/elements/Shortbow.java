package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;

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
