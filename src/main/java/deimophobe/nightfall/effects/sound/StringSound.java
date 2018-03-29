package deimophobe.nightfall.effects.sound;

import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 6/07/17.
 */
public class StringSound implements GlobalSound, LocalSound, PlayerSound {
	private final String sound;
	private final Category category;
	private final float pitch;
	private final float volume;
	
	public StringSound(String sound, Category category) {
		this(sound, category, 1f, 1f);
	}
	
	public StringSound(String sound, Category category, float pitch) {
		this(sound, category, pitch, 1f);
	}
	
	public StringSound(String sound, Category category, float pitch, float volume) {
		this.sound = sound;
		this.category = category;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	@Override
	public void playSound(Location location) {
		location.getWorld().playSound(location, sound, category.getCategory(), volume, pitch);
	}
	
	@Override
	public void playSound(GamePlayer player) {
		player.getPlayer().playSound(player.getLocation(), sound, category.getCategory(), volume, pitch);
	}
	
	@Override
	public void playSound() {
		GameMap.getCurrentMap().getWorld().playSound(GameMap.getCurrentMap().getDwarfSpawn(), sound, category.getCategory(), VOLUME, pitch);
	}
}
