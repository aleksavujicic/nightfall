package deimophobe.nightfall.effects.sound;

import org.bukkit.SoundCategory;

/**
 * Created by Deimophobe on 6/07/17.
 */
enum Category {
	ITEMS(SoundCategory.MASTER),
	MUSIC(SoundCategory.RECORDS),
	SOUND_EFFECT(SoundCategory.MASTER),
	
	;
	
	private final SoundCategory category;
	SoundCategory getCategory() {return category;}
	
	Category(SoundCategory category) {
		this.category = category;
	}
}
