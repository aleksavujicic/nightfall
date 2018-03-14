package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import org.bukkit.Particle;
import org.bukkit.World;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class UntimelyDemise extends AbstractPiece {
	public UntimelyDemise(Dwarf dwarf) {
		super(dwarf);
		dwarf.setPlagueStatus(Dwarf.PlagueStatus.PLAGUED);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		
		World world = dwarf.getWorld();
		world.spawnParticle(Particle.SMOKE_NORMAL, dwarf.getLocation(), 3, 0.2, 0.2, 0.2, 0.01);
		if (halfSec)
			world.spawnParticle(Particle.VILLAGER_ANGRY, dwarf.getEyeLocation().add(0, 0.5, 0), 1, 0.2, 0.2, 0.2);
	}
}
