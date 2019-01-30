package deimophobe.nightfall.game;

import deimophobe.nightfall.cooldown.Updateable;
import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 11/01/19.
 */
public class RestartChecker implements Updateable {
	private final Game game;
	
	private final int frequency;
	
	private final int countThreshold;
	private int count = 0;
	
	public RestartChecker(Game game, int frequency, int countThreshold) {
		this.game = game;
		this.frequency = frequency;
		this.countThreshold = countThreshold;
	}
	
	@Override
	public void update() {
		if (game.getCurrentTick() % frequency != 0) return;
		if (game.getPhase() == Phase.STARTING) return;
		if (Bukkit.getOnlinePlayers().size() != 0) {
			count = 0;
//			game.broadcastDebugMessage("Restart check set to zero.");
			return;
		}
		
		count++;
		//game.broadcastDebugMessage("Restart count incremented to " + count + "/" + countThreshold + ".");
		if (count >= countThreshold) {
//			game.broadcastDebugMessage("Scheduling new game.");
			game.scheduleNewGame();
		}
	}
	
	public void resetCount() {
		count = 0;
	}
}
