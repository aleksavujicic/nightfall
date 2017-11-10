package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class BerserkArmour extends AbstractElement {
	private State state = State.HIGH;
	
	public BerserkArmour(Dwarf dwarf) {
		super(dwarf);
		Armour armour = dwarf.getArmour();
		armour.addModifier(ItemModifierType.ATTACK, 4, "Berserker");
		armour.addModifier(ItemModifierType.SPEED, 10, "Berserker");
		armour.addModifier(ItemModifierType.HEALTH, -4, "Berserker");
		
		if (armour instanceof DwarvenArmour)
			((DwarvenArmour) armour).changeDurability(-40, "Berserker");
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		if (quadSec)
			update();
	}
	
	@Override
	public void damageNotify(DwarfDamage damage) {
		super.damageNotify(damage);
		update();
	}
	
	private void update() {
		if (dwarf.getArmour() instanceof DwarvenArmour) {
			DwarvenArmour armour = (DwarvenArmour) dwarf.getArmour();
			if (!state.isValid(armour)) {
				state = State.getLevel(armour);
				state.apply(dwarf);
			}
		}
	}
	
	private enum State {
		HIGH(0.4, 1, 0, false),
		MED(0.2, 0.4, 1, false),
		LOW(0, 0.2, 2, true)
		;
		
		private final double minArmour;
		private final double maxArmour;
		private final int strLevel;
		private final boolean isBlue;
		State(double minArmour, double maxArmour, int strLevel, boolean isBlue) {
			this.minArmour = minArmour;
			this.maxArmour = maxArmour;
			this.strLevel = strLevel;
			this.isBlue = isBlue;
		}
		
		private boolean isValid(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			return  (minArmour <= frac && frac <= maxArmour);
		}
		
		private static State getLevel(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			for (State state : values()) {
				if (frac >= state.minArmour)
					return state;
			}
			return LOW;
		}
		
		private void apply(Dwarf dwarf) {
			if (strLevel == 0)
				dwarf.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			else
				dwarf.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, strLevel, isBlue);
		}
	}
}
