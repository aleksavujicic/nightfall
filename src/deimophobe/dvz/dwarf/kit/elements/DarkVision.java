package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/03/17.
 */
class DarkVision extends AbstractCooldown {
	public DarkVision(Dwarf dwarf) {
		super(dwarf, 60*20);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (isOffCD() && !sneaking) {
			dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 8*20, 3, true, false, true);
			dwarf.updateVisibility();
			resetCooldown();
		}
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return null;
	}
}
