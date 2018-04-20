package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class ChuggingAle extends AbstractAle {
	private final static int MANA_COST = 25;
	
	public ChuggingAle(Dwarf dwarf) {
		super(dwarf, MANA_COST, 4);
	}
	
	private final static CustomItem ITEM = getAle("chug", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.heal(12);
		playDefaultHealSound();
		dwarf.givePotionEffect(PotionEffectType.SLOW, 20, -2, true, false, false);
	}
}
