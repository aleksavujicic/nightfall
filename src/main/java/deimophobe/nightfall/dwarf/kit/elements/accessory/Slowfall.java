package deimophobe.nightfall.dwarf.kit.elements.accessory;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class Slowfall extends AbstractElement {
	private final ComplexCooldown cooldown;
	private final boolean hasSlow;
	
	private static final double RESISTANCE = 0.8;
	
	public Slowfall(Dwarf dwarf, boolean hasSlow) {
		super(dwarf);
		this.hasSlow = hasSlow;
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
		if (hasSlow) {
			if (cooldown.isAvailable()) {
				//randomSparkle();
			} else if (cooldown.wasUsedWithin(8 * 20)) {
				usedSparkle();
			}
		}
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
		if (!dwarf.hasPotionEffect(PotionEffectType.LEVITATION) && !dwarf.getPlayer().isOnGround()) {
			cooldown.tryUse();
		}
	}
	
	private void randomSparkle() {
		if (Math.random() <= 0.5) {
			double dx = Math.random() - 0.5;
			double dy = Math.random()*0.2;
			double dz = Math.random() - 0.5;
			dwarf.getWorld().spawnParticle(Particle.REDSTONE, dwarf.getLocation().add(dx, dy, dz), 0, 1, 1, 1, 1);
		}
		if (Math.random() <= 0.2) {
			dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation(), 1, 0.3, 0.2, 0.3, 0);
		}
	}
	
	private double theta = 0;
	private void usedSparkle() {
		// Don't show if invisible
		if (dwarf.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
		
		theta = (theta + 0.25) % (2 * Math.PI);
		Vector offset = new Vector(Math.cos(theta), 0, Math.sin(theta)).multiply(0.2);
		dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation().add(offset), 1, 0, 0, 0, 0);
		dwarf.getWorld().spawnParticle(Particle.END_ROD, dwarf.getLocation().add(offset.multiply(-1)), 1, 0, 0, 0, 0);
		
	}
	
	private void slowfall() {
		dwarf.givePotionEffect(PotionEffectType.JUMP, 8 * 20, 3, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.LEVITATION, 8 * 20, -5, true, false, true);
		dwarf.getPlayer().setFallDistance(0);
	}
}
