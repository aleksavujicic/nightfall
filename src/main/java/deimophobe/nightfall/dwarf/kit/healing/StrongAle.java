package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class StrongAle extends AbstractAle {
	private final static int MANA_COST = 400;
	private static final double DAMAGE_RESISTANCE = 0.75;
	public static double getDamageResistance() { return DAMAGE_RESISTANCE; }
	
	public StrongAle(Dwarf dwarf) {
		super(dwarf, MANA_COST, 60);
		dwarf.getArmour().addModifier(ItemModifierType.RESISTANCE, (int) DAMAGE_RESISTANCE *100, "Strongest Potion");
	}
	
	private final static CustomItem ITEM = getAle("strong", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.getDamage().timesMult(1 - DAMAGE_RESISTANCE);
	}
}
