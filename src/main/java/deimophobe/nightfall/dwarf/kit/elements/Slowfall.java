package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class Slowfall extends AbstractElement {
	private final ComplexCooldown cooldown;
	
	private static final double RESISTANCE = 0.8;
	
	public Slowfall(Dwarf dwarf, boolean hasSlow) {
		super(dwarf);
		if (hasSlow) {
			cooldown = new ComplexCooldown(30*20, this::slowfall, null);
		} else {
			cooldown = new ComplexCooldown(-1);
		}
		dwarf.getArmour().addModifier(ItemModifierType.FALL_DAMAGE, (int) (-RESISTANCE*100), "Slowfall");
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		cooldown.update();
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == NaturalDamageType.FALL) {
			if (cooldown.wasUsedWithin(8*20))
				damage.cancel();
			else
				damage.getDamage().timesMult(1 - RESISTANCE);
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (!sneaking && !dwarf.hasPotionEffect(PotionEffectType.LEVITATION) && !dwarf.getPlayer().isOnGround()) {
			cooldown.tryUse();
		}
	}
	
	private void slowfall() {
		dwarf.givePotionEffect(PotionEffectType.JUMP, 8 * 20, 3, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.LEVITATION, 8 * 20, -5, true, false, true);
		dwarf.getPlayer().setFallDistance(0);
	}
}
