package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.PreDamagePriority;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class HeroBase extends AbstractPiece {
	private State state = State.HIGH;
	
	public HeroBase(Dwarf dwarf) {
		super(dwarf);
		
		dwarf.makeBlindImmune();
		dwarf.setPlagueStatus(Dwarf.PlagueStatus.IMMUNE);
	}
	
	@Override
	public void onDamageReceive(DwarfDamage damage) {
		super.onDamageReceive(damage);
		
		damage.addPreDamageHandler(PreDamagePriority.SAFETY_JUICE, () -> {
			double health = dwarf.getPlayer().getHealth();
			if (damage.willKill() || health <= 10) {
				if (dwarf.tryUseMana(100)) {
					dwarf.healMax();
					dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, true);
					dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
					
					damage.softCancel();
				}
			}
		});
		
		damage.multiplyManaDrain(0.25);
		if (damage.getType() == GameDamageType.FALL) {
			damage.getMultiPartDamage().timesMult(0.1);
		}
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
//		update();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
//		state = State.getLevel(dwarf);
//		state.apply(dwarf);
	}
	
	private void update() {
		if (!state.isValid(dwarf)) {
			state = State.getLevel(dwarf);
			state.apply(dwarf);
		}
	}
	
	private enum State {
		HIGH(350, 1000),
		MED(150, 350),
		LOW(-1, 150)
		;
		
		private final int minMana;
		private final int maxMana;
		State(int minMana, int maxMana) {
			this.minMana = minMana;
			this.maxMana = maxMana;
		}
		
		private boolean isValid(Dwarf dwarf) {
			int mana = dwarf.getMana();
			return  (minMana < mana && mana <= maxMana);
		}
		
		private static State getLevel(Dwarf dwarf) {
			int mana = dwarf.getMana();
			for (HeroBase.State state : values()) {
				if (mana >= state.minMana)
					return state;
			}
			return LOW;
		}
		
		private void apply(Dwarf dwarf) {
			switch (this) {
				case HIGH:
					dwarf.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					dwarf.removePotionEffect(PotionEffectType.REGENERATION);
					dwarf.removePotionEffect(PotionEffectType.SPEED);
					break;
				case MED:
					dwarf.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, 3, false, false);
					dwarf.givePermanentPotionEffect(PotionEffectType.REGENERATION, 2,false, false);
					dwarf.givePermanentPotionEffect(PotionEffectType.SPEED, 1,false, false);
					break;
				case LOW:
					dwarf.givePermanentPotionEffect(PotionEffectType.INCREASE_DAMAGE, 6, true, false);
					dwarf.givePermanentPotionEffect(PotionEffectType.REGENERATION, 3,true, false);
					dwarf.givePermanentPotionEffect(PotionEffectType.SPEED, 2,true, false);
					break;
			}
		}
	}
}
