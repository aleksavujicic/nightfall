package deimophobe.nightfall.util;

import deimophobe.nightfall.cooldown.Updateable;
import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 11/12/18.
 */
public class AFKChecker implements Updateable {
	private static final int CHECK_FREQUENCY = 200;
	private static final double DISTANCE_THRESHOLD = 1;
	
	private final GamePlayer player;
	private final int pointsForAFK;
	
	private int updateTick = 0;
	
	private Location lastLocation;
	private int points = 0;
	
	public AFKChecker(GamePlayer player, int secsToAFK) {
		this.player = player;
		
		this.pointsForAFK = Math.max(20*secsToAFK/CHECK_FREQUENCY, 1);
		this.lastLocation = player.getLocation();
	}
	
	@Override
	public void update() {
		updateTick++;
		if (updateTick == CHECK_FREQUENCY) {
			checkForAFK();
			updateTick = 0;
		}
	}
	
	/**
	 * Check if the player is AFK, forcing a check if need be.
	 *
	 * @param checkIfAfk if set to true, and the player is currently set to AFK, it will force a check before returning the value
	 * @return whether the player is AFK or not
	 */
	public boolean isAFK(boolean checkIfAfk) {
		boolean isAfk = isAFKRaw();
		if (isAfk && checkIfAfk) {
			checkForAFK();
			isAfk = isAFKRaw();
		}
		return isAfk;
	}
	
	/**
	 * Resets the AFK score back to zero (which will mean that {@link #isAFK(boolean)}
	 * will return false for at least a short while)
	 */
	public void resetAFK() {
		points = 0;
		updateTick = 0;
		player.sendDebugMsg("Reset AFK");
	}
	
	private void checkForAFK() {
		Location current = player.getLocation();
		
		// Check if different worlds - happens rarely sometimes when players log in
		if (current.getWorld() != lastLocation.getWorld()) {
			player.sendDebugMsg("AFK check - Different worlds (not AFK)");
			points = 0;
			lastLocation = current;
			return;
		}
		
		double difference = current.distance(lastLocation);
		
		if (difference >= DISTANCE_THRESHOLD) {
			// Not afk
			points = 0;
			player.sendDebugMsg("Performed AFK check - Not AFK");
		} else {
			//  Is afk
			points++;
			if (points > pointsForAFK) points = pointsForAFK;
			
			player.sendDebugMsg("Performed AFK check - AFK (points " + points + "/" + pointsForAFK + ")");
		}
		// Save current location
		lastLocation = current;
	}
	
	private boolean isAFKRaw() {
		return points == pointsForAFK;
	}
}
