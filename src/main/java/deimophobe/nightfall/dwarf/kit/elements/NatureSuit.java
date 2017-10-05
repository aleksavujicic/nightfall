package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 5/10/17.
 */
class NatureSuit extends AbstractElement {
	public NatureSuit(Dwarf dwarf) {
		super(dwarf);
		rebuff();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() instanceof NaturalDamageType) {
			switch ((NaturalDamageType) damage.getType()) {
				case CONTACT:
				case DROWNING:
				case FIRE:
				case LAVA:
				case MAGMA_BLOCK:
					damage.cancel();
					break;
				case POISON:
					damage.cancel();
					dwarf.removePotionEffect(PotionEffectType.WITHER);
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
		dwarf.givePermanentPotionEffect(PotionEffectType.FIRE_RESISTANCE, 1);
		dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
	}
}
