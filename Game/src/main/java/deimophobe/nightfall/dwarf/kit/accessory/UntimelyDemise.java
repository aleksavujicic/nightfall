package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class UntimelyDemise extends AbstractPiece {
	public UntimelyDemise(Dwarf dwarf) {
		super(dwarf);
		dwarf.setPlagueStatus(Dwarf.PlagueStatus.PLAGUED);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
	
	@Override
	public void update() {
		super.update();
		
		World world = dwarf.getWorld();
		if (dwarf.everyNthTick(5)) {
			world.spawnParticle(Particle.SMOKE_NORMAL, dwarf.getLocation(), 15, 0.2, 0.2, 0.2, 0.01);
		}
		if (dwarf.everyNthTick(10)) {
			world.spawnParticle(Particle.VILLAGER_ANGRY, dwarf.getEyeLocation().add(0, 0.5, 0), 1, 0.2, 0.2, 0.2);
		}
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		dwarf.givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
	}
}
