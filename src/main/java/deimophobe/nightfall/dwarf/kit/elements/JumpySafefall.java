package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class JumpySafefall extends AbstractCooldown {
	public JumpySafefall(Dwarf dwarf) {
		super(dwarf, 60*20);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == NaturalDamageType.FALL)
			damage.getDamage().timesMult(0.1);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (isOffCD() && !sneaking) {
			dwarf.givePotionEffect(PotionEffectType.JUMP, 8*20, 3, true, false, true);
			resetCooldown();
		}
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return null;
	}
}
