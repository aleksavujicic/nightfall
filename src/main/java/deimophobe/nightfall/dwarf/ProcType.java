package deimophobe.nightfall.dwarf;

import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 6/05/17.
 */
public enum ProcType {
	REGULAR     (60, 1, 5, 0, true, true, true, true),
	DRAGONSKIN  (100, 1, 5, 0, true, true, true, true),
	GRAVEL_PROC (60, 1, 5, 0, true, true, true, true),
	EBOW        (40, 1, 3, 0, true, true, true, false),
	LUMINOUS    (100, 2, 5, 0, true, true, true, false),
	MALICE      (200, 2, 7, 0, true, true, true, true),
	
	VELSWORD    (80, 2, 8, 0, true, true, true, true),
	VELBOW      (140, 2, 8, 0, true, true, true, true),
	
	HORN        (160, 4, 8, 3, true, true, true, true),
	AVENGE      (160, 4, 8, 3, true, true, true, true),
	SHRINE_FALL (220, 3, 3, 3, false, true, false, true),
	
	PROC_BOTTLE (100, 2, 5, 3, true, true, true, true),
	
	RUNEDASH    (12, 10, 15, 0, false, true, false, false),
	
	ROAR        (160, 2, 7, 0, false, false, false, true)
	
	;
	
	private final int duration;
	private final Map<PotionEffectType, Integer> amplifiers = new HashMap<>();
	
	private final boolean showParticles;
	private final boolean showContinuousParticles;
	private final boolean playSound;
	
	ProcType(int duration, int speedLvl, int strLvl, int hasteLvl, boolean showParticles, boolean showContinuousParticles, boolean playSound, boolean overwrite) {
		this.duration = duration;
		this.showParticles = showParticles;
		this.showContinuousParticles = showContinuousParticles;
		this.playSound = playSound;
		
		amplifiers.put(PotionEffectType.SPEED, speedLvl);
		amplifiers.put(PotionEffectType.INCREASE_DAMAGE, strLvl);
		amplifiers.put(PotionEffectType.FAST_DIGGING, hasteLvl);
	}
	
	int getEffectAmplifier(PotionEffectType effectType) {
		return amplifiers.get(effectType);
	}
	
	void onUpdate(Dwarf dwarf) {
		if (showContinuousParticles)
			dwarf.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, dwarf.getEyeLocation(), 1, 0.5, 0.5, 0.5);
	}
	
	void onGive(Dwarf dwarf) {
		if (showParticles)
			dwarf.getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, dwarf.getPlayer().getEyeLocation(), 60, 1, 1, 1);
		
		if (playSound)
			dwarf.playSound("proc", 100f, 1f, false);
	}
	
	public int getDuration() {
		return duration;
	}
}
