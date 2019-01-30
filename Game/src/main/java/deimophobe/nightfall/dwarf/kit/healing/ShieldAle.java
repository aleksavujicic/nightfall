package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.game.entity.ShieldSource;

/**
 * Created by Deimophobe on 4/10/18.
 */
public class ShieldAle extends AbstractAle implements ArmourPiece {
	private static final int MANA_COST = 150;
	
	private final static CustomItem ITEM = getAle("shield", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
	
	public ShieldAle(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.HEALTH, -4, "Temp name ale");
	}
	
	@Override
	protected void heal() {
		super.heal();
		
		dwarf.addMaxShields(ShieldSource.SHIELD_ALE);
	}
}
