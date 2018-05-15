package deimophobe.nightfall.common.database;

import deimophobe.nightfall.common.database.data.PlayerData;

import java.util.UUID;

/**
 * Created by Deimophobe on 9/02/18.
 */
public interface DataIO {
	/**
	 * Loads a {@link PlayerData} object from persistent storage (database).
	 * Should be run async as it database operations are potentially very slow.
	 * @param uuid {@link UUID} of the owner of the PlayerData object.
	 * @return The PlayerData object.
	 */
	PlayerData loadPlayerData(UUID uuid);
	void savePlayerData(PlayerData data);
}
