package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 14/10/18.
 */
class NoFall implements MapFeature {
	
	private final NoFall.NoFallListener listener = new NoFall.NoFallListener();
	private final Set<Material> exceptions;
	
	NoFall() {
		exceptions = new HashSet<>();
		exceptions.add(Material.GRAVEL);
		exceptions.add(Material.SAND);
	}
	
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		Bukkit.getPluginManager().registerEvents(listener, NightfallPlugin.getPlugin());
	}
	
	@Override
	public void deactivate() {
		HandlerList.unregisterAll(listener);
	}
	
	private class NoFallListener implements Listener {
		@EventHandler
		public void onBlockFall(BlockPhysicsEvent event) {
			Material type = event.getBlock().getType();
			if (!type.hasGravity()) return;
			if (exceptions.contains(type)) return;
			
			event.setCancelled(true);
		}
	}
}
