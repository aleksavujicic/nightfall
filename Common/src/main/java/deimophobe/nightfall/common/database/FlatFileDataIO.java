package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.database.data.PlayerData;

import java.util.UUID;

/**
 * Created by Deimophobe on 13/05/18.
 */
public class FlatFileDataIO implements DataIO {
	
	public FlatFileDataIO() {
	}
	
	@Override
	public PlayerData loadPlayerData(UUID uuid) {
		return null;
	}
	
	@Override
	public void savePlayerData(PlayerData data) {
	
	}
}
