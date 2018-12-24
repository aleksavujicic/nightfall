package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class StrongAle extends AbstractAle implements ArmourPiece {
	private static final int MANA_COST = 250;
	private static final double DAMAGE_RESISTANCE = 0.6;
	public static double getDamageResistance() { return DAMAGE_RESISTANCE; }
	
	private final static CustomItem ITEM = getAle("strong", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	public StrongAle(Dwarf dwarf) {
		super(dwarf, MANA_COST, 60);
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.RESISTANCE, (int) DAMAGE_RESISTANCE *100, "Strongest Potion");
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.getMultiPartDamage().timesMult(1 - DAMAGE_RESISTANCE);
	}
}
