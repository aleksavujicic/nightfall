package deimophobe.nightfall.dwarf.kit.hero.lyra;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class FeralInstinct extends AbstractPiece implements ArmourPiece {
	public FeralInstinct(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == GameDamageType.FALL) {
			damage.cancel();
		}
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.SPEED, 20, "Feral Instinct");
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
}
