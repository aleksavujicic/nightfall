package deimophobe.nightfall.dwarf;

import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 6/05/17.
 */
public enum ProcType {
	REGULAR		(60, 1, 2, 0, true, true),
	DRAGONSKIN	(60, 1, 1, 0, true, true),
	GRAVEL_PROC	(60, 1, 4, 0, true, true),
	EBOW		(60, 1, 3, 0, true, true),
	
	HORN		(160, 4, 2, 3, true, true),
	AVENGE		(160, 4, 2, 3, true, true),
	MALICE		(160, 2, 4, 0, true, true),
	SHRINE_FALL	(160, 2, 2, 3, false, false),
	
	RUNEDASH	(12, 1, 10, 0, false, false),
	
	ROAR		(160, 2, 0, 0, false, false),
	
	;
	
	private final int duration;
	private final int speedLvl;
	private final int strLvl;
	private final int hasteLvl;
	
	private final boolean showParticles;
	private final boolean playSound;
	
	ProcType(int duration, int speedLvl, int strLvl, int hasteLvl, boolean showParticles, boolean playSound) {
		this.duration = duration;
		this.speedLvl = speedLvl;
		this.strLvl = strLvl;
		this.hasteLvl = hasteLvl;
		this.showParticles = showParticles;
		this.playSound = playSound;
	}
	
	
	void giveProc(Dwarf dwarf) {
		dwarf.givePotionEffect(PotionEffectType.SPEED, duration, speedLvl, true, false, true);
		dwarf.givePotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, strLvl, true, false, false);
		dwarf.givePotionEffect(PotionEffectType.FAST_DIGGING, duration, hasteLvl, true, false, false);
		
		if (showParticles)
			dwarf.getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, dwarf.getPlayer().getLocation(), 60, 1, 1, 1);
		
		if (playSound)
			dwarf.playSound("proc", 100f, 1f, false);
	}
	
	public int getDuration() {
		return duration;
	}
}
