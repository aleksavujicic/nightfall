package deimophobe.nightfall.dwarf.kit.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class HeartyAle extends AbstractAle implements ArmourPiece {
	private final static int MANA_COST = 100;
	private final static int BONUS_HEARTS = 6;
	
	private final static CustomItem ITEM = getAle("hearty", MANA_COST);
	static { ITEM.applyVariable("hearts", ""+BONUS_HEARTS); }
	@Override public CustomItem getItem() { return ITEM; }
	
	public HeartyAle(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type, MANA_COST);
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.HEALTH, BONUS_HEARTS, "Hearty Ale");
	}
	
	@Override
	protected void heal() {
		super.heal();
		dwarf.givePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4*20, 2, true, false, false);
	}
	
}
