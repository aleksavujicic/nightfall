package deimophobe.nightfall.dwarf.kit.elements.armour;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class BerserkArmour extends AbstractElement {
	private State state = State.HIGH;
	private final static int ATTACK_BONUS = 5;
	
	public BerserkArmour(Dwarf dwarf) {
		super(dwarf);
		Armour armour = dwarf.getArmour();
		armour.addModifier(ItemModifierType.ATTACK, ATTACK_BONUS, "Berserker");
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
			if (!state.isValid(dwarf)) {
				state = State.getLevel(dwarf);
				state.apply(dwarf);
			}
		}
	}

	public static int getAttackBonus() {
		return ATTACK_BONUS;
	}

	private enum State {
		HIGH(350, 1000, 0, false),
		MED(150, 350, 1, false),
		LOW(-1, 150, 3, true)
		;
		
		private final int minMana;
		private final int maxMana;
		private final int strLevel;
		private final boolean isBlue;
		State(int minMana, int maxMana, int strLevel, boolean isBlue) {
			this.minMana = minMana;
			this.maxMana = maxMana;
			this.strLevel = strLevel;
			this.isBlue = isBlue;
		}
		
		private boolean isValid(Dwarf dwarf) {
			int mana = dwarf.getMana();
			return  (minMana < mana && mana <= maxMana);
		}
		
		private static State getLevel(Dwarf dwarf) {
			int mana = dwarf.getMana();
			for (State state : values()) {
				if (mana >= state.minMana)
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
