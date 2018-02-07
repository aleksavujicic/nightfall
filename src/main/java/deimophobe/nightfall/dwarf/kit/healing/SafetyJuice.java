package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class SafetyJuice extends AbstractAle {
	private final static int MANA_COST = 100;
	private final static int AUTO_COST = 120;
	
	public SafetyJuice(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("safety", MANA_COST);
	static {
		ITEM.applyVariable("autocost", ""+AUTO_COST);
	}
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		double health = dwarf.getPlayer().getHealth();
		if (health - damage.getFinalDamage() <= 0.1 || health <= 10) {
			if (dwarf.tryUseMana(AUTO_COST)){
				heal();
				cooldown.reset();
			}
		}
	}
}
