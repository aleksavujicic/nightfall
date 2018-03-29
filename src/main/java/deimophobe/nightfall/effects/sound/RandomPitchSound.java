package deimophobe.nightfall.effects.sound;

import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 6/07/17.
 */
public class RandomPitchSound implements GlobalSound, LocalSound, PlayerSound {
	private final String sound;
	private final Category category;
	private final float min;
	private final float max;
	private final float volume;
	
	
	public RandomPitchSound(String sound, Category category, float min, float max) {
		this(sound, category, min, max, 1f);
	}
	
	public RandomPitchSound(String sound, Category category, float min, float max, float volume) {
		this.sound = sound;
		this.category = category;
		this.min = min;
		this.max = max;
		this.volume = volume;
	}
	
	@Override
	public void playSound(Location location) {
		location.getWorld().playSound(location, sound, category.getCategory(), volume, getPitch());
	}
	
	@Override
	public void playSound(GamePlayer player) {
		player.getPlayer().playSound(player.getLocation(), sound, category.getCategory(), volume, getPitch());
	}
	
	@Override
	public void playSound() {
		GameMap.getCurrentMap().getWorld().playSound(GameMap.getCurrentMap().getDwarfSpawn(), sound, category.getCategory(), VOLUME, getPitch());
	}
	
	private float getPitch() {
		return (float) (Math.random() * min + max - min);
	}
}
