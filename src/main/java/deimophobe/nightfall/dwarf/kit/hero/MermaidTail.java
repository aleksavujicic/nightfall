package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class MermaidTail extends AbstractPiece implements ArmourPiece {
	private boolean fastSwimming = false;
	
	public MermaidTail(Dwarf dwarf) {
		super(dwarf);
		dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
	}
	
	@Override
	public void onArmourEquip() {
		dwarf.getArmour().addModifier(ItemModifierType.DEPTH_STRIDER, 3, "Mermaid Tail");
		dwarf.getArmour().addModifier(ItemModifierType.AQUA_AFFINITY, 1, "Mermaid Tail");
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		
		if (quartSec && fastSwimming && !dwarf.isUnderwater()) {
			removeFastSwim();
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (!sneaking) return;
		dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
		
		if (dwarf.isUnderwater()) {
			if (!fastSwimming) giveFastSwim();
			else removeFastSwim();
		}
	}
	
	private void giveFastSwim() {
		dwarf.givePermanentPotionEffect(PotionEffectType.SPEED, 8);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		fastSwimming = true;
	}
	
	private void removeFastSwim() {
		dwarf.removePotionEffect(PotionEffectType.SPEED);
		dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
		fastSwimming = false;
	}
}
