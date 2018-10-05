package deimophobe.nightfall.common.player;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.LoadoutData;
import deimophobe.nightfall.common.database.data.PlayerData;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.common.player.settings.PlayerSettings;
import deimophobe.nightfall.common.player.stats.PlayerStatistics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class PlayerInfo implements Datable<PlayerData>,SessionData {
	private static final int MAX_SAVED_LOADOUTS = 3;
	
	private final UUID uuid;
	
	private final Cosmetics cosmetics;
	public Cosmetics getCosmetics() { return cosmetics; }
	
	private Loadout loadout;
	public Loadout getLoadout() { return loadout; }
	public void setLoadout(Loadout loadout) { this.loadout = loadout; }
	
	private Loadout[] savedLoadouts = new Loadout[MAX_SAVED_LOADOUTS];
	
	private final PlayerSettings settings;
	public PlayerSettings getSettings() { return settings; }
	
	private final PlayerStatistics statistics;
	public PlayerStatistics getStatistics() { return statistics; }
	
	private int gold;
	
	public PlayerInfo(PlayerData data) {
		this.uuid      = UUID.fromString(data.uuid);
		this.gold      = data.gold;
		
		this.cosmetics   = new Cosmetics(uuid, data.cosmetics);
		this.loadout     = new Loadout(data.loadout);
		this.settings    = new PlayerSettings(data.settings);
		this.statistics  = new PlayerStatistics(data.statistics);
		
		int numSavedLoadouts = data.savedLoadouts.size();
		if (numSavedLoadouts > MAX_SAVED_LOADOUTS) {
			NightfallCommonPlugin.logger().warning("Player with uuid '" + uuid + "' had more than " + MAX_SAVED_LOADOUTS + " saved loadouts! Discarded extras.");
			numSavedLoadouts = MAX_SAVED_LOADOUTS;
		}
		
		for (int i=0; i<MAX_SAVED_LOADOUTS; i++) {
			Loadout loadout;
			if (i < numSavedLoadouts) {
				LoadoutData loadoutData = data.savedLoadouts.get(i);
				loadout = new Loadout(loadoutData);
			} else {
				loadout = new Loadout();
			}
			savedLoadouts[i] = loadout;
		}
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	@Override
	public PlayerData toData() {
		PlayerData data = new PlayerData();
		
		data.uuid      = this.uuid.toString();
		data.gold      = this.gold;
		
		data.cosmetics       = this.cosmetics.toData();
		data.loadout         = this.loadout.toData();
		data.settings        = this.settings.toData();
		data.statistics      = this.statistics.toData();
		
		data.savedLoadouts = Stream.of(savedLoadouts)
				.map(Loadout::toData)
				.collect(Collectors.toList());
		
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
	
	public Loadout getSavedLoadout(int slot) {
		checkArgument(slot < MAX_SAVED_LOADOUTS, "Slot must be less than " + MAX_SAVED_LOADOUTS);
		return savedLoadouts[slot];
	}
	
	public void setSavedLoadout(int slot, Loadout loadout) {
		checkArgument(slot < MAX_SAVED_LOADOUTS, "Slot must be less than " + MAX_SAVED_LOADOUTS);
		savedLoadouts[slot] = loadout;
	}
}
