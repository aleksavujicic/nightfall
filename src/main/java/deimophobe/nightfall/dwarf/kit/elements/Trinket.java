package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Trinket extends AbstractAle {
	Trinket(Dwarf dwarf) {
		super(dwarf, 200);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ale.trinket", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 10 ,true, true, true);
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, 720000, 4 ,true, true, true);
		dwarf.playSound("entity.evocation_illager.cast_spell", 1f, 1.5f, false);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
}
