package deimophobe.nightfall;

import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 16/10/18.
 */
public class VoteManager {
	
	
	private Vote activeVote = null;
	
	public void yayVote(Player player) {
		activeVote.yayVote(player);
	}
	
	public void nayVote(Player player) {
		activeVote.nayVote(player);
	}
}
