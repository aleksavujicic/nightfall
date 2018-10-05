package deimophobe.nightfall.common.player.stats;

import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 5/10/18.
 */
class FixedComponent implements BookComponent {
	private final String text;
	
	FixedComponent(String text) {
		this.text = text;
	}
	
	@Override
	public String createString(Player player, PlayerStatistics stats) {
		return text;
	}
}
