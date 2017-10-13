package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 5/10/17.
 */
class StrongAle extends AbstractAle {
	private final static int MANA_COST = 400;
	
	public StrongAle(Dwarf dwarf) {
		super(dwarf, MANA_COST, 60);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, 2, "Strong Ale");
	}
	
	private final static CustomItem ITEM = getAle("strong", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.getDamage().timesMult(0.25);
	}
}
