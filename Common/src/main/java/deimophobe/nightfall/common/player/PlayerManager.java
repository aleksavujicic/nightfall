package deimophobe.nightfall.common.player;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.DataIO;
import deimophobe.nightfall.common.database.data.PlayerData;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.common.player.settings.PlayerSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class PlayerManager {
	public static PlayerManager getManager() { return NightfallCommonPlugin.getPlugin().getPlayerManager(); }
	
	private final NightfallCommonPlugin plugin;
	private final DataIO dataIO;
	
	private final BukkitRunnable autosaver;
	
	public PlayerManager(NightfallCommonPlugin plugin) {
		this.plugin = plugin;
		this.dataIO = plugin.getDataIO();
		
		DataListener listener = new DataListener();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadPlayerInfo(player.getUniqueId());
		}
		
		int autosaveFreq = plugin.getConfig().getInt("database.autosave", 300);
		if (autosaveFreq <= 0) {
			plugin.getLogger().warning("Autosaver disabled!");
			this.autosaver = null;
			return;
		}
		
		this.autosaver = new BukkitRunnable() {
			@Override
			public void run() {
				saveAll();
			}
		};
		autosaver.runTaskTimerAsynchronously(plugin, autosaveFreq*20, autosaveFreq*20);
	}
	
	public void onDisable() {
		if (autosaver != null) autosaver.cancel();
		saveAll();
	}
	
	// ----- DATA LOADING -----
	private final Map<UUID, PlayerInfo> playerInfoMap = new ConcurrentHashMap<>();
	
	private void loadPlayerInfo(UUID playerID) {
		checkNotNull(playerID, "UUID must not be null.");
		checkArgument(!playerInfoMap.containsKey(playerID), "Cannot load PlayerData of player whose PlayerData is already loaded.");
		
		PlayerData data = dataIO.loadPlayerData(playerID);
		PlayerInfo info = new PlayerInfo(data);
		playerInfoMap.put(playerID, info);
	}
	
	private void savePlayerInfo(UUID playerID) {
		checkNotNull(playerID, "UUID must not be null.");
		checkArgument(playerInfoMap.containsKey(playerID), "PlayerData must be loaded to save it.");
		
		PlayerInfo info = playerInfoMap.get(playerID);
		PlayerData data = info.toData();
		dataIO.savePlayerData(data);
	}
	
	private void unloadPlayerInfo(UUID playerID, boolean save) {
		checkNotNull(playerID, "UUID must not be null.");
		checkArgument(playerInfoMap.containsKey(playerID), "PlayerData must be loaded to unload it.");
		
		if (save) savePlayerInfo(playerID);
		playerInfoMap.remove(playerID);
	}
	
	private void saveAll() {
		if (playerInfoMap.isEmpty()) return;
		
		// Setup logger/format
		Logger logger = plugin.getLogger();
		final NumberFormat format = new DecimalFormat("#.##");
		
		// Log start
		logger.info("Saving player data...");
		long startTime = System.nanoTime();
		
		// Save
		for (UUID uuid : playerInfoMap.keySet()) {
			savePlayerInfo(uuid);
		}
		
		// Log end
		long endTime = System.nanoTime();
		long timeTaken = endTime - startTime;
		double timeMilli = (double)timeTaken/1000000;
		logger.info("Saved player data (took " + format.format(timeMilli) + " ms)");
	}
	
	// Note that this may take a while.
	public void loadMissingData() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			UUID uuid = player.getUniqueId();
			if (playerInfoMap.containsKey(uuid)) continue;
			
			NightfallCommonPlugin.logger().warning("Loading missing PlayerData of player '" + player.getName() + "'");
			loadPlayerInfo(uuid);
		}
	}
	
	
	public PlayerInfo getPlayerInfo(UUID uuid) {
		checkNotNull(uuid, "UUID must not be null.");
		checkArgument(playerInfoMap.containsKey(uuid), "PlayerData must be loaded to get it.");
		
		return playerInfoMap.get(uuid);
	}
	
	public PlayerInfo getPlayerInfo(Player player) {
		checkNotNull(player, "Player must not be null.");
		
		return getPlayerInfo(player.getUniqueId());
	}
	
	public Cosmetics getCosmetics(Player player) {
		PlayerInfo info = getPlayerInfo(player);
		return info.getCosmetics();
	}
	
	public Loadout getLoadout(Player player) {
		PlayerInfo info = getPlayerInfo(player);
		return info.getLoadout();
	}
	
	public PlayerSettings getSettings(Player player) {
		PlayerInfo info = getPlayerInfo(player);
		return info.getSettings();
	}
	
	
	private class DataListener implements Listener {
		private Set<UUID> loadingUUIDs = new HashSet<>();
		
		@EventHandler
		private void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
			UUID uuid = event.getUniqueId();
			
			if (loadingUUIDs.contains(uuid)) {
				event.disallow(
						AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
						"You cannot join right now, please try again in a few seconds."
				);
				return;
			}
			
			if (playerInfoMap.containsKey(uuid)) {
				NightfallCommonPlugin.logger().warning("Player logged in while PlayerData already loaded.");
			} else {
				loadPlayerInfo(uuid);
			}
		}
		
		@EventHandler
		private void onPlayerLeave(PlayerQuitEvent event) {
			final UUID uuid = event.getPlayer().getUniqueId();
			final String name = event.getPlayer().getName();
			loadingUUIDs.add(uuid);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					boolean isOnline = Bukkit.getPlayer(uuid) != null;
					if (isOnline) {
						NightfallCommonPlugin.logger().warning(
								"Player online after PlayerQuitEvent - not unloading data (Player '" + name + "')."
						);
						loadingUUIDs.remove(uuid);
						return;
					}
					
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								unloadPlayerInfo(uuid, true);
							} finally {
								loadingUUIDs.remove(uuid);
							}
						}
					}.runTaskAsynchronously(plugin);
				}
			}.runTask(plugin);
		}
	}
}
