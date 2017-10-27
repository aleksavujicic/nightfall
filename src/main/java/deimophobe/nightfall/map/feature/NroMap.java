package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.event.DwarfCreateEvent;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 10/07/17.
 */
public class NroMap implements MapFeature {
	
	private GameMap map;
	private BukkitRunnable waterChecker;
	private boolean waterCheckerCancelled;
	private GameStartListener listener = new GameStartListener();
	
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		this.map = map;
		
		World world = map.getWorld();
		world.setSpawnFlags(false, true);
		Bukkit.getPluginManager().registerEvents(listener, NightfallPlugin.getPlugin());
		
		waterChecker = new BukkitRunnable() {
			@Override
			public void run() {
				if (Game.getGame().getPhase() != Phase.STARTING) {
					waterCheckerCancelled = true;
					this.cancel();
					return;
				}
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (isLobbyPlayerUnderwater(player)) {
						Block below = player.getLocation().subtract(0,1,0).getBlock();
						Block above = player.getLocation().add(0,1,0).getBlock();
						if (below.isLiquid() || above.isLiquid()) {
							player.sendTitle("", ChatColor.AQUA +"Now is not the time for a swim!", 5, 80, 20);
							Game.getGame().resetPlayer(player);
						}
					}
				}
			}
		};
		waterChecker.runTaskTimer(NightfallPlugin.getPlugin(), 0, 20);
		waterCheckerCancelled = false;
	}
	
	@Override
	public void deactivate() {
		HandlerList.unregisterAll(listener);
		if (!waterCheckerCancelled)
			waterChecker.cancel();
	}
	
	private boolean isLobbyPlayerUnderwater(Player player) {
		Block lowerBlock = player.getLocation().getBlock();
		Block upperBlock = lowerBlock.getRelative(BlockFace.UP);
		return Game.getGame().isLobbyPlayer(player) && (lowerBlock.isLiquid() || upperBlock.isLiquid());
	}
	
	private class GameStartListener implements Listener {
		@EventHandler
		public void gameStart(PhaseChangeEvent event) {
			if (event.getPhase() != Phase.BUILD) return;
			
			// remove command blocks
			World world = map.getWorld();
			world.setSpawnFlags(false, false);
			world.getBlockAt(-684, 66, -28).setType(Material.STONE);
			world.getBlockAt(-684, 66, -23).setType(Material.STONE);
			world.getBlockAt(-684, 66, -22).setType(Material.STONE);
			world.getBlockAt(-685, 66, -22).setType(Material.STONE);
			world.getBlockAt(-686, 66, -22).setType(Material.STONE);
		}
		
		@EventHandler
		public void giveBlessing(DwarfCreateEvent event) {
			event.getDwarf().getArmour().addModifier(ItemModifierType.DEPTH_STRIDER, 3, "Mermaid's Blessing");
		}
	}
}
