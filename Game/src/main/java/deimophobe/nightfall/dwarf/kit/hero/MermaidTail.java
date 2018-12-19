package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.SwimPiece;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class MermaidTail extends AbstractPiece implements ArmourPiece, SwimPiece {
	
	public MermaidTail(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (damage.getType() == GameDamageType.DROWNING) damage.cancel();
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.DEPTH_STRIDER, 3, "Mermaid Tail", ArmourSlot.FEET);
		armour.addModifier(ItemModifierType.AQUA_AFFINITY, 1, "Mermaid Tail", ArmourSlot.FEET);
		
		Player player = dwarf.getPlayer();
		updateSwimState(player.isSwimming());
	}
	
	@Override
	public void onSwim(boolean swimming) {
		updateSwimState(swimming);
	}
	
	private void updateSwimState(boolean swimming) {
		if (swimming) {
			giveFastSwim();
		} else {
			removeFastSwim();
		}
	}
	
	private void giveFastSwim() {
		dwarf.givePermanentPotionEffect(PotionEffectType.SPEED, 5);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
	}
	
	private void removeFastSwim() {
		dwarf.removePotionEffect(PotionEffectType.SPEED);
		dwarf.removePotionEffect(PotionEffectType.NIGHT_VISION);
		dwarf.removePotionEffect(PotionEffectType.WATER_BREATHING);
	}
}
