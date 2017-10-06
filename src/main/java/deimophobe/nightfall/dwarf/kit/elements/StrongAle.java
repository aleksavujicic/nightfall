package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;

/**
 * Created by Deimophobe on 5/10/17.
 */
class StrongAle extends AbstractAle {
	public StrongAle(Dwarf dwarf) {
		super(dwarf, 400, 60);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, 2, "Strong Ale");
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("ale.strong", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		damage.getDamage().timesMult(0.25);
	}
}
