package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 5/11/17.
 */
class MapWinterland implements MapFeature {
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 50, 10, 10, 10, 0);
				}
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 0, 1);
	}
	
	@Override
	public void deactivate() {
	
	}
}
