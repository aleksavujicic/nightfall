package deimophobe.nightfall.common.database;

import java.util.UUID;

/**
 * Created by Deimophobe on 9/02/18.
 */
public interface DataHandler {
	PlayerInfo getInfo(UUID uuid);
	void saveInfo(PlayerInfo info);
}
