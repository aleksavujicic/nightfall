package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 14/03/17.
 */
class MagicCoil extends DwarvenItem {
	
	private static final int DURATION = 60*20;
	private static final int CHANGEOVER_DURATION = 5*20;
	private static final Set<Buff> BUFFS = new HashSet<>();
	static {
		BUFFS.add(new Buff(PotionEffectType.DAMAGE_RESISTANCE, 2));
		BUFFS.add(new Buff(PotionEffectType.FIRE_RESISTANCE, 1));
		BUFFS.add(new Buff(PotionEffectType.SLOW, -1));
		BUFFS.add(new Buff(PotionEffectType.NIGHT_VISION, 1));
		BUFFS.add(new Buff(PotionEffectType.INCREASE_DAMAGE, 2));
		BUFFS.add(new Buff(PotionEffectType.REGENERATION, 4));
	}
	
	private Buff currentBuff;
	
	protected MagicCoil(Dwarf dwarf) {
		super(dwarf, null);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				giveBuff();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 10, DURATION);
	}
	
	private void giveBuff() {
		Buff buff = getRandomBuff();
		
		if (buff != null) {
			buff.giveBuff(dwarf);
			if (currentBuff != null)
				currentBuff.removeBuff(dwarf);
			
			currentBuff = buff;
		} else {
			if (currentBuff != null)
				currentBuff.giveBuff(dwarf);
		}
	}
	
	private Buff getRandomBuff() {
		Set<Buff> newBuffs = new HashSet<>();
		for (Buff buff : BUFFS) {
			if (!buff.hasBuff(dwarf))
				newBuffs.add(buff);
		}
		
		return Misc.getRandom(newBuffs);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		if (currentBuff != null && !currentBuff.hasBuff(dwarf))
			currentBuff.giveBuff(dwarf);
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
			dwarf.givePotionEffect(type, DURATION + CHANGEOVER_DURATION, amplifier, true, true, true);
		}
		
		private void removeBuff(Dwarf dwarf) {
			dwarf.getPlayer().removePotionEffect(type);
		}
	}
}
