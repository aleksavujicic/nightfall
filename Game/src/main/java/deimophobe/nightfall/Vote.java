package deimophobe.nightfall;

import deimophobe.nightfall.common.command.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 16/10/18.
 */
public class Vote {
	private final VoteType voteType;
	
	private final Set<Player> yayVoters;
	
	public Vote(VoteType voteType) {
		this.voteType = voteType;
		this.yayVoters = new HashSet<>();
	}
	
	public void yayVote(Player player) {
		boolean added = yayVoters.add(player);
		if (added) {
			MessageUtil.sendMessage(player, "You have voted yay.");
		} else {
			MessageUtil.sendMessage(player, "You have already voted yay.");
		}
	}
	
	public void nayVote(Player player) {
		boolean removed = yayVoters.remove(player);
		if (removed) {
			MessageUtil.sendMessage(player, "You have voted nay.");
		} else {
			MessageUtil.sendMessage(player, "You have already voted nay");
		}
	}
	
	private void endVote() {
		double passRatio = (double) yayVoters.size() / Bukkit.getOnlinePlayers().size();
		voteType.finishedVote(passRatio);
	}
}
