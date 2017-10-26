package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.CustomItem;

/**
 * Created by Deimophobe on 22/01/17.
 */
class JimmyJuice extends AbstractAle {
	private final static int MANA_COST = 100;
	private final static int AUTO_COST = 120;
	
	JimmyJuice(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("jj", MANA_COST);
	static {
		ITEM.applyVariable("autocost", ""+AUTO_COST);
	}
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		double health = dwarf.getPlayer().getHealth();
		if (health - damage.getFinalDamage() <= 0.1 || health <= 16) {
			if (dwarf.tryUseMana(AUTO_COST)){
				heal();
				cooldown.reset();
			}
		}
	}
}
