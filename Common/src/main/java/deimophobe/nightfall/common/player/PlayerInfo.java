package deimophobe.nightfall.common.player;

import deimophobe.nightfall.common.database.data.PlayerData;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;

import java.util.UUID;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class PlayerInfo {
	private final UUID uuid;
	
	private final Cosmetics cosmetics;
	public Cosmetics getCosmetics() { return cosmetics; }
	
	public PlayerInfo(PlayerData data) {
		this.uuid = UUID.fromString(data.uuid);
		this.cosmetics = new Cosmetics(uuid, data.cosmetics);
	}
	
	public PlayerData toData() {
		PlayerData data = new PlayerData();
		data.uuid = uuid.toString();
		data.cosmetics = cosmetics.toData();
		
		return data;
	}
}
