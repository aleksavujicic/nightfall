package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.elements.AbstractCooldown;
import deimophobe.dvz.dwarf.kit.elements.AbstractElement;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HealerTotem extends AbstractElement {
	
	private boolean active;
	
	public HealerTotem(Dwarf dwarf) {
		super(dwarf);
	}
	
	private void activate() {
		active = true;
		dwarf.givePermanentPotionEffect(PotionEffectType.SLOW, 100);
		dwarf.givePermanentPotionEffect(PotionEffectType.WEAKNESS, 100);
		dwarf.givePermanentPotionEffect(PotionEffectType.JUMP, -100);
		dwarf.givePermanentPotionEffect(PotionEffectType.GLOWING, 1);
		dwarf.getPlayer().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
	}
	
	private void deactivate() {
		active = false;
		dwarf.removePotionEffect(PotionEffectType.SLOW);
		dwarf.removePotionEffect(PotionEffectType.WEAKNESS);
		dwarf.removePotionEffect(PotionEffectType.JUMP);
		dwarf.removePotionEffect(PotionEffectType.GLOWING);
		dwarf.getPlayer().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (sneaking) activate();
		else deactivate();
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (active && type == DamageType.FALL) return -1;
		if (active) return damage*2;
		
		return damage;
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (quartSec && active)
			dwarf.useMana(5);
	}
}
