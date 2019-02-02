package deimophobe.nightfall.common.player.stats;

import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.PlayerStatsData;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 5/10/18.
 */
public class PlayerStatistics implements Datable<PlayerStatsData> {
	private int gamesPlayed;
	
	public PlayerStatistics(PlayerStatsData data) {
		this.gamesPlayed = data.gamesPlayed;
	}
	
	@Override
	public PlayerStatsData toData() {
		PlayerStatsData data = new PlayerStatsData();
		data.gamesPlayed = this.gamesPlayed;
		
		return data;
	}
	
	
	// Static Helper
	
	public static PlayerStatistics getStatistics(Player player) {
		return PlayerManager.getManager().getPlayerInfo(player).getStatistics();
	}
	
	
	// Stat changes
	
	public int getGamesPlayed() {
		return gamesPlayed;
	}
	
	public void incrementGameCount() {
		gamesPlayed++;
	}
}
