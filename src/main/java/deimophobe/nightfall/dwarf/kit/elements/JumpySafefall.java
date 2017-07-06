package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.GameEntity;
import deimophobe.nightfall.damage.DamageType;
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
	public double onGotHit(GameEntity monster, DamageType type, double damage) {
		if (type == DamageType.FALL) {
			damage *= 0.1;
		}
		return damage;
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
