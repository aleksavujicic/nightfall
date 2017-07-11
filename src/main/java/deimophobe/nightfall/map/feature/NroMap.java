package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Created by Deimophobe on 10/07/17.
 */
public class NroMap implements MapFeature {
	
	private GameMap map;
	private GameStartListener listener = new GameStartListener();
	
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		 this.map = map;
		
		Bukkit.broadcastMessage("NRO");
		
		World world = map.getWorld();
		world.setSpawnFlags(false, true);
		Bukkit.getPluginManager().registerEvents(listener, NightfallPlugin.getPlugin());
	}
	
	@Override
	public void deactivate() {
		
	}
	
	private class GameStartListener implements Listener {
		@EventHandler
		public void gameStart(PhaseChangeEvent event) {
			if (event.getPhase() != Phase.BUILD) return;
			
			Bukkit.broadcastMessage("BUILD");
			
			// remove command blocks
			World world = map.getWorld();
			world.setSpawnFlags(false, false);
			world.getBlockAt(-684, 66, -28).setType(Material.STONE);
			world.getBlockAt(-684, 66, -23).setType(Material.STONE);
			world.getBlockAt(-684, 66, -22).setType(Material.STONE);
			world.getBlockAt(-685, 66, -22).setType(Material.STONE);
			world.getBlockAt(-686, 66, -22).setType(Material.STONE);
			
			HandlerList.unregisterAll(this);
		}
	}
}
