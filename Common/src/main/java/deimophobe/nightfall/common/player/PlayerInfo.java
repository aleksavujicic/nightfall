package deimophobe.nightfall.common.player;

import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.PlayerData;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;

import java.util.UUID;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class PlayerInfo implements Datable<PlayerData> {
	private final UUID uuid;
	
	private final Cosmetics cosmetics;
	public Cosmetics getCosmetics() { return cosmetics; }
	
	private final Loadout loadout;
	public Loadout getLoadout() { return loadout; }
	
	public PlayerInfo(PlayerData data) {
		this.uuid = UUID.fromString(data.uuid);
		this.cosmetics = new Cosmetics(uuid, data.cosmetics);
		this.loadout = new Loadout(data.loadout);
	}
	
	@Override
	public PlayerData toData() {
		PlayerData data = new PlayerData();
		data.uuid = uuid.toString();
		data.cosmetics = cosmetics.toData();
		data.loadout = loadout.toData();
		
		return data;
	}
}
