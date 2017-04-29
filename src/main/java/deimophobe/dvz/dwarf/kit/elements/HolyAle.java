package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HolyAle extends AbstractAle {
	HolyAle(Dwarf dwarf) {
		super(dwarf, 100);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ale.holy", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		super.heal();
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 4, true, true, true);
	}
}
