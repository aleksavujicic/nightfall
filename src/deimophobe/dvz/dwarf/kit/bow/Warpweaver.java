package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.blocks.timedblock.LampBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Warpweaver extends Bow {
	Warpweaver(Dwarf dwarf) {
		super(dwarf, BowType.WARPWEAVER, 30);
	}
	
	private Location warpSpot;
	private boolean warping = false;
	private int cooldown = 0;
	
	private final static int TELEPORT_TIME = 20*20;
	private final static int MAX_COOLDOWN = 40*20;
	
	@Override
	public void update() {
		if (warping) {
			cooldown++;
			if (cooldown >= TELEPORT_TIME) {
				cooldown = MAX_COOLDOWN;
				teleportBack();
			}
		} else {
			if (cooldown > 0)
				cooldown--;
		}
	}
	
	@Override
	public float fractionComplete() {
		if (warping)
			return 1 - (float)cooldown/TELEPORT_TIME;
		else
			return 1 - (float)cooldown/MAX_COOLDOWN;
	}
	
	@Override
	public void onProjectileLand(Projectile proj, Block hitBlock) {
		if (!warping && cooldown <= 0 && !isHoldingItem()) {
			warping = true;
			
			warpSpot = dwarf.getLocation();
			Location newSpot = proj.getLocation().add(0, 0.25, 0);
			
			dwarf.teleportTo(newSpot.setDirection(warpSpot.getDirection()));
			
			World world = warpSpot.getWorld();
			world.spawnParticle(Particle.SPELL_WITCH, warpSpot, 20, 0.5, 0.5, 0.5);
			world.spawnParticle(Particle.SPELL_WITCH, newSpot, 20, 0.5, 0.5, 0.5);
		}
	}
	
	private void teleportBack() {
		warping = false;
		
		Location curSpot = dwarf.getLocation();
		dwarf.teleportTo(warpSpot);
		
		World world = warpSpot.getWorld();
		world.spawnParticle(Particle.SPELL_WITCH, warpSpot, 20, 0.5, 0.5, 0.5);
		world.spawnParticle(Particle.SPELL_WITCH, curSpot, 20, 0.5, 0.5, 0.5);
	}
}
