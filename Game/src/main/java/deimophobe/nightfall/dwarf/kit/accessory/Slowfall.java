package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class Slowfall extends AbstractPiece implements ArmourPiece {
	private static final double RESISTANCE = 0.8;
	private boolean active = false;
	
	public Slowfall(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (active) {
			usedSparkle();
			Player player = dwarf.getPlayer();
			player.setFallDistance(0);
			if (player.isOnGround()) deactivate();
		}
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == GameDamageType.FALL) {
			damage.getMultiPartDamage().timesMult(1 - RESISTANCE);
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (!sneaking) return;
		
		if (!dwarf.getPlayer().isOnGround()) {
			toggleActive();
		}
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.FALL_DAMAGE, (int) (-RESISTANCE*100), "Slowfall", ArmourSlot.FEET);
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
	
	private void toggleActive() {
		if (active) {
			deactivate();
		} else {
			activate();
		}
	}
	
	private void activate() {
		active = true;
		dwarf.givePermanentPotionEffect(PotionEffectType.LEVITATION, -5);
		dwarf.getPlayer().setFallDistance(0);
	}
	
	private void deactivate() {
		active = false;
		dwarf.removePotionEffect(PotionEffectType.LEVITATION);
	}
}
