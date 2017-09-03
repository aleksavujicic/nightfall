package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;

/**
 * Created by Deimophobe on 22/01/17.
 */
class JimmyJuice extends AbstractAle {
	JimmyJuice(Dwarf dwarf) {
		super(dwarf, 100);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ale.jj", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		double health = dwarf.getPlayer().getHealth();
		if (health - damage.getCurrentDamage() <= 0.1 || health <= 16) {
			if (dwarf.tryUseMana(120)) {
				heal();
			}
		}
	}
}
