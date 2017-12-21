package deimophobe.nightfall.effects;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 27/10/17.
 */
class LargeGoldMineMaker extends SmallGoldMineMaker {
	private static final int NUM_PARTICLES = 6;
	
	@Override
	public void playEffect(GamePlayer player, Block block) {
		super.playEffect(player, block);
		
		GameMap map = GameMap.getCurrentMap();
		Location start = block.getLocation().add(0.5,0.5,0.5);
		Set<GoldParticle> particles = new HashSet<>();
		for (int i = 0; i <NUM_PARTICLES; i++) {
			particles.add(new GoldParticle(start, 2*Math.PI*i/NUM_PARTICLES, player));
		}
		
		new BukkitRunnable() {
			int num = 100;
			
			@Override
			public void run() {
				map.mineGold();
				Sounds.DWARF_MINE_GOLD.playSound(player);
				
				for (GoldParticle particle : particles)
					particle.update();
				
				num--;
				if (num <= 0)
					this.cancel();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 10, 2);
	}
	
	private class GoldParticle {
		private Location location;
		private double theta;
		private final GamePlayer player;
		
		private GoldParticle(Location location, double theta, GamePlayer player) {
			this.location = location.clone();
			this.theta = theta;
			this.player = player;
		}
		
		private void update() {
			theta = (theta+0.1) % (2*Math.PI);
			
			Location target = player.getLocation().add(0,0.75,0).add(0.75*Math.cos(theta), 0, 0.75*Math.sin(theta));
			Vector offset = target.subtract(location).toVector();
			if (offset.length() >= 0.3)
				offset.normalize().multiply(0.3);
			location.add(offset);
			
			spawnGoldParticle(location);
		}
	}
}
