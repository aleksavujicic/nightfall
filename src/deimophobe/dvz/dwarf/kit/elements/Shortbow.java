package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 31/03/17.
 */
class Shortbow extends AbstractBow {
	
	public Shortbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("bow.shortbow", Slot.MAIN_HAND);
	@Override public ItemStack getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "SHORTBOW";}
	@Override public int getPower() {return 30;}
}
