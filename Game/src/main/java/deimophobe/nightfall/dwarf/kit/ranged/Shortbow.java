package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 31/03/17.
 */
public class Shortbow extends AbstractBow {
	
	public Shortbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 30;
	private final static CustomItem ITEM = getBow("shortbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "SHORTBOW";}
	@Override public int getPower() {return POWER;}
}
