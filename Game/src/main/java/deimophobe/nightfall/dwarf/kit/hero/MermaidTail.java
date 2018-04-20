package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class MermaidTail extends AbstractPiece implements ArmourPiece {
	
	private boolean isInSwimmingMode;
	
	public MermaidTail(Dwarf dwarf) {
		super(dwarf);
		dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
		isInSwimmingMode = dwarf.isUnderwater();
		updateSwimState();
	}
	
	@Override
	public void onArmourEquip() {
		dwarf.getArmour().addModifier(ItemModifierType.DEPTH_STRIDER, 3, "Mermaid Tail", ArmourSlot.FEET);
		dwarf.getArmour().addModifier(ItemModifierType.AQUA_AFFINITY, 1, "Mermaid Tail", ArmourSlot.FEET);
	}
	
	@Override
	public void update() {
		super.update();
		updateSwimState();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (!sneaking) return;
		dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
		updateSwimState();
	}
	
	private void updateSwimState() {
		boolean underwater = dwarf.isUnderwater();
		if (underwater && !isInSwimmingMode) {
			giveFastSwim();
		} else if (!underwater && isInSwimmingMode) {
			removeFastSwim();
		}
	}
	
	private void giveFastSwim() {
		dwarf.givePermanentPotionEffect(PotionEffectType.SPEED, 8);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		isInSwimmingMode = true;
	}
	
	private void removeFastSwim() {
		dwarf.removePotionEffect(PotionEffectType.SPEED);
		dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
		isInSwimmingMode = false;
	}
}
