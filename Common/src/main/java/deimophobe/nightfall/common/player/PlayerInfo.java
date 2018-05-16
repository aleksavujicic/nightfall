package deimophobe.nightfall.common.player;

import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.PlayerData;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.common.player.settings.PlayerSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class PlayerInfo implements Datable<PlayerData> {
	private final UUID uuid;
	
	private final Cosmetics cosmetics;
	public Cosmetics getCosmetics() { return cosmetics; }
	
	private final Loadout loadout;
	public Loadout getLoadout() { return loadout; }
	
	private final PlayerSettings settings;
	public PlayerSettings getSettings() { return settings; }
	
	private int gold;
	
	public PlayerInfo(PlayerData data) {
		this.uuid      = UUID.fromString(data.uuid);
		this.gold      = data.gold;
		
		this.cosmetics = new Cosmetics(uuid, data.cosmetics);
		this.loadout   = new Loadout(data.loadout);
		this.settings  = new PlayerSettings(data.settings);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	@Override
	public PlayerData toData() {
		PlayerData data = new PlayerData();
		
		data.uuid      = this.uuid.toString();
		data.gold      = this.gold;
		
		data.cosmetics = this.cosmetics.toData();
		data.loadout   = this.loadout.toData();
		data.settings  = this.settings.toData();
		
		return data;
	}
	
	public void giveGold(int amount) {
		checkArgument(amount >= 0, "Can only give a positive amount of gold.");
		gold += amount;
	}
	
	public void removeGold(int amount) {
		checkArgument(amount >= 0, "Can only take a positive amount of gold.");
		gold -= amount;
	}
	
	public int getGoldAmount() {
		return gold;
	}
	
}
