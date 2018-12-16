package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import deimophobe.nightfall.map.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Deimophobe on 6/07/17.
 */
class TeleportPad implements MapFeature {
	private final TeleListener listener;
	private final List<Teleport> teleports;
	
	TeleportPad() {
		listener = new TeleListener();
		teleports = new ArrayList<>();
	}
	
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		Bukkit.getPluginManager().registerEvents(listener, NightfallPlugin.getPlugin());
		
		Set<String> keys = config.getKeys(false);
		if (keys.size() == 0)
			throw new InvalidMapConfigException("You must have at least one tp pad.");
		
		for (String key : keys)
			teleports.add(new Teleport(map, config.getConfigurationSection(key)));
	}
	
	@Override
	public void deactivate() {
		HandlerList.unregisterAll(listener);
	}
	
	
	private class Teleport {
		private final Region from;
		private final Location to;
		
		private Teleport(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
			if (!config.contains("from"))
				throw new InvalidMapConfigException("Teleport must contain a from region.");
			if (!config.contains("to"))
				throw new InvalidMapConfigException("Teleport must contain a to location.");
			
			this.from = Region.createRegion(map, config.getConfigurationSection("from"));
			this.to = map.getLocation(config, "to");
		}
		
		private boolean tryTeleport(Player player) {
			if (from.continsEntity(player)) {
				player.teleport(to);
				player.playSound(to, "entity.enderman.teleport", 1f, 1f);
				return true;
			} else {
				return false;
			}
		}
	}
	
	private class TeleListener implements Listener {
		@EventHandler
		public void onShift(PlayerToggleSneakEvent event) {
			if (!event.isSneaking()) return;
			
			for (Teleport teleport : teleports) {
				boolean success = teleport.tryTeleport(event.getPlayer());
				if (success) return;
			}
		}
	}
}
