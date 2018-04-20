package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 5/11/17.
 */
class Snowfall implements MapFeature {
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		double size = config.getDouble("size", 10);
		double velocity = config.getDouble("velocity", 0);
		int amount = config.getInt("amount", 50);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					Block block = player.getLocation().getBlock();
					int skyLight = block.getLightFromSky();
					int headSkyLight = block.getRelative(0,1,0).getLightFromSky();
					
					skyLight = Math.max(skyLight, headSkyLight);
					
					if (skyLight >= 11)
						player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), ((skyLight - 10)*amount)/5, size, size, size, velocity);
				}
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
	}
	
	@Override
	public void deactivate() {
	
	}
}
