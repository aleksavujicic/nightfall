package deimophobe.nightfall.dwarf.kit.elements.healing;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.healing.AbstractAle;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class Trinket extends AbstractAle {
	private final static int MANA_COST = 200;
	
	public Trinket(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("trinket", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 10 ,true, true, true);
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, 720000, 4 ,true, true, true);
		dwarf.playSound("block.enchantment_table.use", 1f, 1.1f, true);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
}
