package deimophobe.nightfall.dwarf.light;

import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.CooldownHolder;
import deimophobe.nightfall.cooldown.Expirable;
import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

/**
 * Created by Deimophobe on 7/12/18.
 */
public class DwarfEyes implements Updateable {
	private final Dwarf dwarf;
	private final DwarfVision vision;
	private final Collection<LightSource> sources;
	private int currentVision = 0;
	private boolean immune = false;
	
	public DwarfEyes(Dwarf dwarf) {
		this.dwarf = dwarf;
		this.vision = new DwarfVision();
		this.sources = new HashSet<>();
		
		for (LightSource source : LightSource.DEFAULT_SOURCES) {
			addSource(source);
		}
	}
	
	public void addSource(LightSource source) {
		sources.add(source);
	}
	
	public void makeImmune() {
		immune = true;
	}
	
	@Override
	public void update() {
		// Count vision and update
		vision.resetVisionCount();
		sources.forEach(source -> source.apply(dwarf, vision));
		
		int targetVision = vision.getVision();
		int blindDuration = vision.getBlindnessDuration();
		boolean forceBlind = vision.isForceBlind();
		
		currentVision = Math.max(targetVision, currentVision - 1);
		currentVision = Math.max(currentVision, 0);
		
//		dwarf.sendDebugMsg(
//				String.format("Vision: %s/%s", currentVision, targetVision)
//		);
		
		// Set blindness if necessary
		if (forceBlind) {
			setPermaBlind();
		} else if (blindDuration > 0) {
			setTempBlind(blindDuration);
		} else if (currentVision == 0 && !immune) {
			setPermaBlind();
		} else {
			removeBlind();
		}
		
		// Update any sources (and expire them too)
		sources.forEach(source -> {
			if (source instanceof Updateable) {
				((Updateable) source).update();
			}
		});
		sources.removeIf(source -> {
			if (source instanceof Expirable) {
				return ((Expirable) source).hasExpired();
			}
			return false;
		});
	}
	
	private void setPermaBlind() {
		if (dwarf.getPotionEffectDuration(BLINDNESS) < 100) {
			dwarf.sendDebugMsg("Set perma blind");
			dwarf.givePermanentPotionEffect(BLINDNESS, 1);
		}
	}
	
	private void setTempBlind(int duration) {
		if (dwarf.getPotionEffectDuration(BLINDNESS) < duration) {
			dwarf.sendDebugMsg("Set temp blind: " + duration);
			dwarf.givePotionEffect(BLINDNESS, duration, 1, true, false, true);
		}
	}
	
	private void removeBlind() {
		if (dwarf.hasPotionEffect(BLINDNESS)) {
			dwarf.sendDebugMsg("Remove blind");
			dwarf.removePotionEffect(BLINDNESS);
		}
	}
}
