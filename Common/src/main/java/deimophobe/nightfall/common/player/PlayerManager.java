package deimophobe.nightfall.common.player;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.DataIO;
import deimophobe.nightfall.common.database.data.PlayerData;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class PlayerManager {
	public static PlayerManager getManager() { return NightfallCommonPlugin.getPlugin().getPlayerManager(); }
	
	private final NightfallCommonPlugin plugin;
	private final DataIO dataIO;
	
	public PlayerManager(NightfallCommonPlugin plugin) {
		this.plugin = plugin;
		this.dataIO = plugin.getDataIO();
		
		DataListener listener = new DataListener();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			loadPlayerInfo(player.getUniqueId());
		}
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
	
	public void saveAll() {
		for (UUID uuid : playerInfoMap.keySet()) {
			savePlayerInfo(uuid);
		}
	}
	
	
	private class DataListener implements Listener {
		
		@EventHandler
		private void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
			UUID uuid = event.getUniqueId();
			PlayerManager.this.loadPlayerInfo(uuid);
		}
		
		@EventHandler
		private void onPlayerLeave(PlayerQuitEvent event) {
			final UUID uuid = event.getPlayer().getUniqueId();
			new BukkitRunnable() {
				@Override
				public void run() {
					PlayerManager.this.unloadPlayerInfo(uuid, true);
				}
			}.runTaskAsynchronously(plugin);
		}
	}
}
