package deimophobe.nightfall.effects.sound;

/**
 * Created by Deimophobe on 6/07/17.
 */
public class Sounds {
	public static final GlobalSound DWARF_ITEM_HORN = new StringGlobalSound("horn", Category.ITEMS);
	public static final PlayerSound DWARF_MINE_GOLD = new RandomPitchSound("block.note.bell", Category.SOUND_EFFECT, 0.8f, 1.9f);
	
	public static final GlobalSound MONSTER_DOOM_DRUM = new RandomPitchSound("drum", Category.MUSIC, 0.7f, 1f);
	public static final GlobalSound MONSTER_DOOM_DRUM_MANAMA = new StringGlobalSound("manamadrum", Category.MUSIC);
}
