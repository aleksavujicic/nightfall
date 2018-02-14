package deimophobe.nightfall.dwarf.kit.hero.lyra;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class FeralInstinct extends AbstractPiece implements ArmourPiece {
	public FeralInstinct(Dwarf dwarf) {
		super(dwarf);
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
	public void onArmourEquip() {
		dwarf.getArmour().addModifier(ItemModifierType.SPEED, 20, "Feral Instinct");
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
}
