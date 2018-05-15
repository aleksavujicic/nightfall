package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.data.PlayerData;

import java.util.UUID;

/**
 * Never saves data, only loads defaults.
 * Created by Deimophobe on 9/02/18.
 */
public class DefaultDataIO implements DataIO {
	
	DefaultDataIO(NightfallCommonPlugin plugin) {
		plugin.getLogger().warning("Using default dataIO. No data will be saved.");
	}
	
	@Override
	public PlayerData loadPlayerData(UUID uuid) {
		return new PlayerData(uuid);
	}
	
	@Override
	public void savePlayerData(PlayerData data) {}
}
