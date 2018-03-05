package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class NatureSuit extends AbstractPiece implements ArmourPiece {
	public NatureSuit(Dwarf dwarf) {
		super(dwarf);
		dwarf.getArmour().addModifier(ItemModifierType.DEPTH_STRIDER, 1, "Nature Suit");
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (dwarf.getArmour().isArmoured()) {
			switch (damage.getType()) {
				case CONTACT:
				case DROWNING:
				case FIRE:
				case LAVA:
				case MAGMA_BLOCK:
					damage.cancel();
					break;
				case WITHER:
					damage.cancel();
					dwarf.removePotionEffect(PotionEffectType.WITHER);
					break;
				case POISON:
					damage.cancel();
					dwarf.removePotionEffect(PotionEffectType.POISON);
					break;
			}
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		rebuff();
	}
	
	private void rebuff() {
		if (dwarf.getArmour().isArmoured()) {
			dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
			dwarf.givePermanentPotionEffect(PotionEffectType.FIRE_RESISTANCE, 1);
			dwarf.getPlayer().setFireTicks(0);
		}
	}
	
	@Override
	public void onArmourEquip() {
		rebuff();
	}
}
