package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 5/10/17.
 */
public class NatureSuit extends AbstractPiece implements ArmourPiece {
	public NatureSuit(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		if (dwarf.getArmour().isArmoured()) {
			switch (damage.getType()) {
				case CONTACT:
				case DROWNING:
				case FIRE:
				case LAVA:
				case MAGMA_BLOCK:
					damage.cancel();
					break;
					
				case WITHER:
				case POISON:
					if (Game.getGame().getPhase() != Phase.PLAGUE) {
						damage.cancel();
						dwarf.removeAllPoisons();
					}
					break;
			}
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		rebuff();
	}
	
	private void rebuff() {
		if (dwarf.getArmour().isArmoured()) {
			dwarf.givePermanentPotionEffect(PotionEffectType.WATER_BREATHING, 1);
			dwarf.givePermanentPotionEffect(PotionEffectType.FIRE_RESISTANCE, 1);
			dwarf.removeFire();
		}
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		rebuff();
		armour.addModifier(ItemModifierType.NATURE_SUIT, 1);
		armour.addModifier(ItemModifierType.DEPTH_STRIDER, 1, "Taproot Armour", ArmourSlot.FEET);
	}
}
