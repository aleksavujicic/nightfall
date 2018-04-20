package deimophobe.nightfall.util.music;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 27/01/18.
 */
class PlayedNote {
	private final float pitch;
	private final long duration; //ticks
	
	PlayedNote(int note, long duration) {
		this.pitch = (float) Math.pow(2, (double) (note - 12)/12);
		this.duration = duration;
	}
	
	void play(Supplier<Location> locationSupplier, String sound, float volume, Queue<PlayedNote> remainingNotes) {
		Location location = locationSupplier.get();
		if (location == null) return;
		
		location.getWorld().playSound(location, sound, volume, pitch);
		
		if (!remainingNotes.isEmpty()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					remainingNotes.poll().play(locationSupplier, sound, volume, remainingNotes);
				}
			}.runTaskLater(NightfallPlugin.getPlugin(), duration);
		}
	}
}
