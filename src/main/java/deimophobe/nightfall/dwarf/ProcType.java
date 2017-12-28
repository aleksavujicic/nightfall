package deimophobe.nightfall.dwarf;

import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 6/05/17.
 */
public enum ProcType {
	REGULAR     (60, 1, 5, 0, true, true, true, true),
	DRAGONSKIN  (100, 1, 5, 0, true, true, true, true),
	GRAVEL_PROC (60, 1, 5, 0, true, true, true, true),
	EBOW        (40, 1, 3, 0, true, true, true, false),
	LUMINOUS    (80, 2, 5, 0, true, true, true, false),
	MALICE      (200, 2, 7, 0, true, true, true, true),
	
	VELSWORD    (80, 2, 8, 0, true, true, true, true),
	VELBOW      (140, 2, 8, 0, true, true, true, true),
	
	HORN        (160, 4, 8, 3, true, true, true, true),
	AVENGE      (160, 4, 8, 3, true, true, true, true),
	SHRINE_FALL (160, 2, 3, 3, false, true, false, true),
	
	RUNEDASH    (12, 1, 15, 0, false, true, false, false),
	
	ROAR        (160, 2, 7, 0, false, false, false, true)
	
	;
	
	private final int duration;
	private final int speedLvl;
	private final int strLvl;
	private final int hasteLvl;
	
	private final boolean showParticles;
	private final boolean showContinuousParticles;
	private final boolean playSound;
	
	private final boolean overwrite;
	
	ProcType(int duration, int speedLvl, int strLvl, int hasteLvl, boolean showParticles, boolean showContinuousParticles, boolean playSound, boolean overwrite) {
		this.duration = duration;
		this.speedLvl = speedLvl;
		this.strLvl = strLvl;
		this.hasteLvl = hasteLvl;
		this.showParticles = showParticles;
		this.showContinuousParticles = showContinuousParticles;
		this.playSound = playSound;
		this.overwrite = overwrite;
	}
	
	boolean shouldShowCtsParticles() {
		return showContinuousParticles;
	}
	
	boolean giveProc(Dwarf dwarf) {
		boolean success = dwarf.givePotionEffect(PotionEffectType.SPEED, duration, speedLvl, true, false, overwrite);
		if (!success) return false;
		
		dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, strLvl, true, false, overwrite);
		dwarf.givePotionEffect(PotionEffectType.FAST_DIGGING, duration, hasteLvl, true, false, overwrite);
		
		if (showParticles)
			dwarf.getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, dwarf.getPlayer().getEyeLocation(), 60, 1, 1, 1);
		
		if (playSound)
			dwarf.playSound("proc", 100f, 1f, false);
		
		return true;
	}
	
	public int getDuration() {
		return duration;
	}
}
