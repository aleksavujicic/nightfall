package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 11/03/17.
 */
class Wildfire extends Bow {
	Wildfire(Dwarf dwarf) {
		super(dwarf, BowType.WILDFIRE);
	}
	
	private int cooldown = 0;
	private final static int MAX_COOLDOWN = 5;
	
	@Override
	public void update() {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (cooldown == 0 && dwarf.hasArrows(1)) {
			cooldown = MAX_COOLDOWN;
			
			Location spawnLoc = dwarf.getEyeLocation();
			Vector looking = spawnLoc.getDirection();
			
			spawnLoc.add(looking);
			looking.normalize().multiply(FLAME_VELOCITY);
			
			new Flame(spawnLoc, looking);
			
			dwarf.useArrows(1);
		}
	}
	
	private static final int FLAME_LIFE = 40;
	private static final int FLAME_DELAY = 2;
	private static final double FLAME_RADIUS = 1;
	private static final double FLAME_VELOCITY = 0.2;
	
	private class Flame {
		private int lifeLeft = FLAME_LIFE;
		private Location position;
		private final Vector velocity;
		
		private Flame(Location position, Vector velocity) {
			this.position = position;
			this.velocity = velocity;
			
			new BukkitRunnable() {
				@Override
				public void run() {
					lifeLeft -= FLAME_DELAY;
					
					position.add(velocity);
					
					position.getWorld().spawnParticle(Particle.FLAME, position, 10, 0.25, 0.25, 0.25, 0);
					
					if (lifeLeft <= 0) this.cancel();
				}
			}.runTaskTimer(Game.getGame().getPlugin(), 0, FLAME_DELAY);
		}
	}
}
