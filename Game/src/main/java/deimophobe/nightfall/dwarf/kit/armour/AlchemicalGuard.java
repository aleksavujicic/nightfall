package deimophobe.nightfall.dwarf.kit.armour;

import com.google.common.collect.Sets;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.RepeaterCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.*;
import deimophobe.nightfall.util.ArmourSlot;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 14/03/17.
 */
public class AlchemicalGuard extends AbstractPiece implements CooldownPiece, ArmourPiece {
	
	private static final Buff WEAK_REGEN = new Buff(PotionEffectType.REGENERATION, 3);
	
	private static final int DURATION = 60*20;
	private static final int CHANGEOVER_DURATION = 5*20;
	private static final Set<Buff> BUFFS = Sets.newHashSet(
			new Buff(PotionEffectType.DAMAGE_RESISTANCE, 2),
			new Buff(PotionEffectType.SLOW, -1),
			new Buff(PotionEffectType.FIRE_RESISTANCE, 1),
			new Buff(PotionEffectType.INCREASE_DAMAGE, 3),
			new Buff(PotionEffectType.HEALTH_BOOST, 3),
			new Buff(PotionEffectType.FAST_DIGGING, 3),
			new Buff(PotionEffectType.NIGHT_VISION, 1) {
				@Override
				public boolean canRollBuff(Dwarf dwarf) {
					return super.canRollBuff(dwarf) && !dwarf.hasKitPiece(KitPieceType.DARKVISION);
				}
			},
			new Buff(PotionEffectType.REGENERATION, 4) {
				@Override
				public void giveBuff(Dwarf dwarf, int time) {
					if (dwarf.hasKitPiece(KitPieceType.STRONG_ALE)) {
						WEAK_REGEN.giveBuff(dwarf, time);
					} else {
						super.giveBuff(dwarf, time);
					}
				}
			}
	);
	
	private Buff currentBuff = null;
	private RepeaterCooldown buffGiver = new RepeaterCooldown(DURATION, this::selectNewBuff);
	
	public AlchemicalGuard(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void update() {
		buffGiver.update();
	}
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.ALCHEMICAL_GUARD, 1);
		forceSelectNewBuff();
	}
	
	private void forceSelectNewBuff() {
		if (currentBuff != null) currentBuff.removeBuff(dwarf);
		
		selectNewBuff();
		buffGiver.reset();
	}
	
	private void selectNewBuff() {
		dwarf.playSound("block.note_block.chime", 10f, 1f, false);
		dwarf.playSound("item.bottle.fill", 10f, 1.5f, false);
		
		// Choose random buff
		Set<Buff> newBuffs = new HashSet<>();
		for (Buff buff : BUFFS) {
			if (buff.canRollBuff(dwarf)) {
				newBuffs.add(buff);
			}
		}
		Buff buff = Misc.getRandom(newBuffs);
		
		// Give buff if possible
		if (buff != null) {
			buff.giveBuff(dwarf);
			
			currentBuff = buff;
		} else {
			if (currentBuff != null) {
				currentBuff.giveBuff(dwarf);
			}
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (dwarf.isDebugMode() && sneaking) {
			forceSelectNewBuff();
			buffGiver.reset();
		}
		
		if (currentBuff != null) {
			currentBuff.giveBuff(dwarf, buffGiver.getTimeRemaining() - 1);
		}
	}
	
	@Override
	public float getCooldown() {
		return buffGiver.getCooldown();
	}
	
	private static class Buff {
		private final PotionEffectType type;
		private final int amplifier;
		
		private Buff(PotionEffectType type, int amplifier) {
			this.type = type;
			this.amplifier = amplifier;
		}
		
		public boolean canRollBuff(Dwarf dwarf) {
			return !dwarf.getPlayer().hasPotionEffect(type);
		}
		
		private void giveBuff(Dwarf dwarf) {
			giveBuff(dwarf, DURATION);
		}
		
		public void giveBuff(Dwarf dwarf, int time) {
			dwarf.givePotionEffect(type, time + CHANGEOVER_DURATION, amplifier, true, false, true);
		}
		
		private void removeBuff(Dwarf dwarf) {
			dwarf.getPlayer().removePotionEffect(type);
		}
		
	}
}
