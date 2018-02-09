package deimophobe.nightfall.common.database;

import java.util.UUID;

/**
 * Created by Deimophobe on 9/02/18.
 */
public class DefaultHandler implements DataHandler {
	@Override
	public PlayerInfo getInfo(UUID uuid) {
		return new PlayerInfo(uuid);
	}
	
	@Override
	public void saveInfo(PlayerInfo info) {
	
	}
}
