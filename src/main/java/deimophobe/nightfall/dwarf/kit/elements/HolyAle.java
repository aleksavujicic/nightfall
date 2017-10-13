package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HolyAle extends AbstractAle {
	private final static int MANA_COST = 100;
	
	HolyAle(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("holy", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		super.heal();
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 4, true, true, true);
	}
}
