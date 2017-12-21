package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.event.PhaseChangeEvent;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashSet;

/**
 * Created by Deimophobe on 21/10/17.
 */
public class MapNurah implements MapFeature {
	
	private Location center;
	private MapNurah.GameEndListener listener = new MapNurah.GameEndListener();
	private double popDistance;
	
	private BukkitRunnable lobbyPopper;
	private Location lobbyCenter;
	private double lobbyRadius;
	
	@Override
	public void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException {
		center = map.getLocation(config, "center");
		popDistance = config.getDouble("pop-dist",50);
		map.getWorld().setGameRuleValue("doFireTick", "false");
		Bukkit.getPluginManager().registerEvents(listener, NightfallPlugin.getPlugin());
		
		
		new BukkitRunnable() {
			@Override
			public void run() {
				double dx = 400*Math.random() - 200;
				double dz = 400*Math.random() - 200;
				double dy = 100*Math.random() - 60;
				
				particlePop(center.clone().add(dx,dy,dz), true);
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 1,10);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (int i=0; i<20; i++) {
					double dx = 400 * Math.random() - 200;
					double dz = 400 * Math.random() - 200;
					double dy = 100 * Math.random() - 60;
					
					particlePop(center.clone().add(dx, dy, dz), false);
				}
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 1,1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Math.random() <= 0.1) {
					center.getWorld().playSound(center, "block.lava.ambient", 1000f, 1f);
				}
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 1,15*20);
		
		
		lobbyCenter = map.getLocation(config, "lobby.center");
		lobbyRadius = config.getDouble("lobby.radius",10);
		lobbyPopper = new BukkitRunnable() {
			@Override
			public void run() {
				for (int i=0; i<1; i++) {
					double dx = lobbyRadius * Math.random() - lobbyRadius/2;
					double dz = lobbyRadius * Math.random() - lobbyRadius/2;
					double dy = lobbyRadius/2 * Math.random() - lobbyRadius/4;
					
					particlePop(lobbyCenter.clone().add(dx, dy, dz), false);
				}
			}
		};
		lobbyPopper.runTaskTimer(NightfallPlugin.getPlugin(), 1,1);
	}
	
	@Override
	public void deactivate() {
		HandlerList.unregisterAll(listener);
	}
	
	
	private void particlePop(Location loc, boolean force) {
		Material type = loc.getBlock().getRelative(0,-1,0).getType();
		if (force || type.isSolid()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (force || loc.distance(player.getLocation()) <= popDistance) {
					player.spawnParticle(Particle.LAVA, loc, 10, 0, 0, 0, 0.3);
					player.playSound(loc, "block.lava.pop", 1f, 1f);
				}
			}
		}
	}
	
	
	
	private class GameEndListener implements Listener {
		@EventHandler
		public void gameEnd(PhaseChangeEvent event) {
			if (event.getPhase() == Phase.BUILD) {
				lobbyPopper.cancel();
			}
			
			
			if (event.getPhase() == Phase.END)
				lavaExplode();
		}
	}
	
	private void lavaExplode() {
		new BukkitRunnable() {
			@Override
			public void run() {
				increaseLavaLevel();
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 60, 60);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				double dx = 40*Math.random() - 20;
				double dz = 40*Math.random() - 20;
				double dy = 20*Math.random() + lavaLevel;
				
				explode(center.clone().add(dx,dy,dz));
			}
		}.runTaskTimer(NightfallPlugin.getPlugin(), 30, 2);
	}
	
	private void explode(Location loc) {
		World world = loc.getWorld();
		world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
		world.spawnParticle(Particle.FLAME, loc, 30, 2, 2, 2, 0.3);
		world.spawnParticle(Particle.LAVA, loc, 50, 2, 2, 2, 0.3);
		
		world.playSound(loc, "entity.generic.burn", 2f, 0.5f);
		world.playSound(loc, "entity.ghast.shoot", 2f, 0.5f);
	}
	
	private int lavaLevel = 0;
	private static final BlockFace[] OFFSETS = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
	private static final int MAX_RECURSION = 3000;
	private void increaseLavaLevel() {
		lavaLevel++;
		
		// x-z flood fill
		LinkedHashSet<Block> toChange = new LinkedHashSet<>();
		toChange.add(center.clone().add(0, lavaLevel, 0).getBlock());
		
		int i = 0;
		while (!toChange.isEmpty()) {
			Block changee = toChange.iterator().next();
			changee.setType(Material.STATIONARY_LAVA);
			for (BlockFace offset : OFFSETS) {
				Block newBlock = changee.getRelative(offset);
				if (!toChange.contains(newBlock) && newBlock.getType() == Material.AIR)
					toChange.add(newBlock);
			}
			toChange.remove(changee);
			
			i++;
			if (i >= MAX_RECURSION) {
				Bukkit.getLogger().warning("Nurah Map - Hit lava place recursion limit.");
				break;
			}
		}
	}
}
