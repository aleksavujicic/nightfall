package deimophobe.nightfall.dwarf.kit.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.armour.DwarvenArmour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 1/11/17.
 */
public class BerserkArmour extends AbstractPiece {
	private State state = State.HIGH;
	private final static int ATTACK_BONUS = 5;
	public static int getAttackBonus() {
		return ATTACK_BONUS;
	}
	
	public BerserkArmour(Dwarf dwarf) {
		super(dwarf);
		Armour armour = dwarf.getArmour();
		armour.addModifier(ItemModifierType.ATTACK, ATTACK_BONUS, "Berserker");
		armour.addModifier(ItemModifierType.SPEED, 10, "Berserker");
		armour.addModifier(ItemModifierType.HEALTH, -4, "Berserker");
		
		if (armour instanceof DwarvenArmour)
			((DwarvenArmour) armour).changeDurability(-30, "Berserker");
	}
	
	@Override
	public void update() {
		super.update();
		updateState();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		state = State.getLevel(dwarf);
		state.apply(dwarf);
	}
	
	private void updateState() {
		if (!state.isValid(dwarf)) {
			state = State.getLevel(dwarf);
			state.apply(dwarf);
		}
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
			if (strLevel == 0) {
				dwarf.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			} else {
				dwarf.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, strLevel, isBlue, false);
			}
		}
	}
}
