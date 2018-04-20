package deimophobe.nightfall.effects.sound;

import deimophobe.nightfall.map.GameMap;

/**
 * Created by Deimophobe on 6/07/17.
 */
public class StringGlobalSound implements GlobalSound {
	private final String sound;
	private final Category category;
	private final float pitch;
	
	public StringGlobalSound(String sound, Category category) {
		this(sound, category, 1f);
	}
	
	public StringGlobalSound(String sound, Category category, float pitch) {
		this.sound = sound;
		this.category = category;
		this.pitch = pitch;
	}
	
	@Override
	public void playSound() {
		GameMap.getCurrentMap().getWorld().playSound(GameMap.getCurrentMap().getDwarfSpawn(), sound, category.getCategory(), VOLUME, pitch);
	}
}
