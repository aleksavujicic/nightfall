package deimophobe.nightfall.effects.sound;

import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 25/03/18.
 */
public class NoSound implements GlobalSound, LocalSound, PlayerSound {
	@Override public void playSound() {}
	@Override public void playSound(Location location) {}
	@Override public void playSound(GamePlayer player) {}
}
