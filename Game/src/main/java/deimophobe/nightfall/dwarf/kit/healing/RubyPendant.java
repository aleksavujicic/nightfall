package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class RubyPendant extends AbstractAle {
	private final static int MANA_COST = 200;
	
	public RubyPendant(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("pendant", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 10 ,true, true, true);
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, 720000, 4 ,true, true, true);
		dwarf.playSound("block.enchantment_table.use", 1f, 1.1f, true);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
}
