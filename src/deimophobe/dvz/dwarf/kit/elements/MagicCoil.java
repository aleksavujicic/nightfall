package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 14/03/17.
 */
class MagicCoil extends AbstractCooldown {
	
	private static final int DURATION = 60*20;
	private static final int CHANGEOVER_DURATION = 5*20;
	private static final Set<Buff> BUFFS = new HashSet<>();
	static {
		BUFFS.add(new Buff(PotionEffectType.DAMAGE_RESISTANCE, 2));
		BUFFS.add(new Buff(PotionEffectType.FIRE_RESISTANCE, 1));
		BUFFS.add(new Buff(PotionEffectType.SLOW, -1));
		BUFFS.add(new Buff(PotionEffectType.NIGHT_VISION, 1));
		BUFFS.add(new Buff(PotionEffectType.INCREASE_DAMAGE, 3));
		BUFFS.add(new Buff(PotionEffectType.REGENERATION, 4));
		BUFFS.add(new Buff(PotionEffectType.HEALTH_BOOST, 3));
		BUFFS.add(new Buff(PotionEffectType.FAST_DIGGING, 3));
	}
	
	private Buff currentBuff;
	
	protected MagicCoil(Dwarf dwarf) {
		super(dwarf, DURATION);
		
		// Update in half a second
		resetCooldown();
		reduceCooldown(DURATION - 10);
	}
	
	@Override
	protected void onOffCD() {
		// Choose random buff
		Set<Buff> newBuffs = new HashSet<>();
		for (Buff buff : BUFFS) {
			if (!buff.hasBuff(dwarf))
				newBuffs.add(buff);
		}
		Buff buff = Misc.getRandom(newBuffs);
		
		// Give buff if possible
		if (buff != null) {
			buff.giveBuff(dwarf);
			
			currentBuff = buff;
		} else {
			if (currentBuff != null)
				currentBuff.giveBuff(dwarf);
		}
		
		resetCooldown();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (currentBuff != null && !currentBuff.hasBuff(dwarf))
			currentBuff.giveBuff(dwarf, getCooldown());
	}
	
	@Override
	public ItemStack getCooldownToggleItem() {
		return null;
	}
	
	private static class Buff {
		private final PotionEffectType type;
		private final int amplifier;
		
		private Buff(PotionEffectType type, int amplifier) {
			this.type = type;
			this.amplifier = amplifier;
		}
		
		private boolean hasBuff(Dwarf dwarf) {
			return dwarf.getPlayer().hasPotionEffect(type);
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
