package deimophobe.nightfall.effects;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 10/03/17.
 */
class ArmourEffectMaker implements PlayerEffectMaker {
	@Override
	public void playEffect(GamePlayer player) {
		checkNotNull(player, "GamePlayer must not be null.");
		World world = player.getLocation().getWorld();
		
		// PLAY SOUNDS!
		Location location = player.getLocation();
		world.playSound(location, "entity.firework.large_blast", 1, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				world.playSound(location, "entity.firework.twinkle", 1, 1);
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 20);
		
		
		// SHOW PARTICLES!
		Location bodyCentre = player.getEyeLocation().add(0, -0.5, 0);
		double velocity = 0.2;
		for (int i=0; i<8; i++) {
			for (int j=0; j<4; j++) {
				double theta = 2*Math.PI*i/8;
				double phi = Math.PI*j/4;
				
				double vx = velocity*Math.sin(theta)*Math.cos(phi);
				double vy = velocity*Math.sin(theta)*Math.sin(phi);
				double vz = velocity*Math.cos(theta);
				world.spawnParticle(Particle.END_ROD, bodyCentre, 0, vx, vy, vz, 1);
			}
		}
		
		// SHOW MORE PARTICLES!
		player.addUpdateable(new LifetimeExpireable(60) {
			@Override
			public void update() {
				super.update();
				
				Location center = player.getEyeLocation().subtract(0, 0.5, 0);
				Misc.spawnColouredParticles(center, 2, 0.75, 0.75, 0.75, 0.977, 0.977, 0.039);
			}
		});
	}
}
