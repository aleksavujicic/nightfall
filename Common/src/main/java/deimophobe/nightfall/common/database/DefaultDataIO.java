package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.database.data.PlayerData;

import java.util.UUID;

/**
 * Created by Deimophobe on 9/02/18.
 */
public class DefaultDataIO implements DataIO {
	@Override
	public PlayerData loadPlayerData(UUID uuid) {
		return new PlayerData(uuid);
	}
	
	@Override
	public void savePlayerData(PlayerData data) {
	
	}
}
