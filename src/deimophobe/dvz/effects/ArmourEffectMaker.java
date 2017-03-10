package deimophobe.dvz.effects;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 10/03/17.
 */
class ArmourEffectMaker implements EffectMaker {
	
	@Override
	public void playEffect(final Location location) {
		throw new UnsupportedOperationException("Armour effect can only be applied on a player!");
	}
	
	@Override
	public void playEffect(GamePlayer player) {
		World world = player.getLocation().getWorld();
		
		// PLAY SOUNDS!
		world.playSound(player.getLocation(), "entity.firework.large_blast", 1, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				world.playSound(player.getLocation(), "entity.firework.twinkle", 1, 1);
			}
		}.runTaskLater(Game.getGame().getPlugin(), 20);
		
		
		// SHOW PARTICLES!
		Location bodyCentre = player.getEyeLocation().add(0, -0.5, 0);
		for (int i=0; i<10; i++) {
			for (int j=0; j<5; j++) {
				double velocity = 0.2;
				double theta = 2*Math.PI*i/8;
				double phi = Math.PI*j/4;
				
				double vx = velocity*Math.sin(theta)*Math.cos(phi);
				double vy = velocity*Math.sin(theta)*Math.sin(phi);
				double vz = velocity*Math.cos(theta);
				world.spawnParticle(Particle.END_ROD, bodyCentre, 0, vx, vy, vz, 1);
			}
		}
		
		// SHOW MORE PARTICLES!
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				for (int i=0; i<7; i++) {
					double dx = 1.5 * Math.random() - 0.75;
					double dy = 1.5 * Math.random() - 1.25;
					double dz = 1.5 * Math.random() - 0.75;
					world.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(dx, dy, dz), 0, 250d/256, 250d/256, 10d/256, 1);
				}
				count++;
				if (count >= 15)
					cancel();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 0, 4);
	}
}
