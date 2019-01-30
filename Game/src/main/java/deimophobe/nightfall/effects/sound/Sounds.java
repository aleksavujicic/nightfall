package deimophobe.nightfall.effects.sound;

/**
 * Created by Deimophobe on 6/07/17.
 */
public class Sounds {
	public static final PlayerSound DWARF_INTRO_MUSIC = new StringSound("division", Category.MUSIC, 1.0f, 1000f);
	public static final PlayerSound DWARF_ITEM_EBOW_GIVE_PROC = new StringSound("proc", Category.ITEMS, 1.2f, 0.6f);
	public static final GlobalSound DWARF_ITEM_HORN = new StringGlobalSound("horn", Category.ITEMS);
	public static final LocalSound DWARF_MAKE_ARMOUR = new StringSound("entity.enderdragon.hurt", Category.SOUND_EFFECT, 0.5f, 0.5f);
	public static final LocalSound DWARF_MINE_ARMOUR = new StringSound("block.anvil.land", Category.SOUND_EFFECT, 0.5f, 1.0f);
	public static final PlayerSound DWARF_MINE_GOLD = new RandomPitchSound("block.note_block.bell", Category.SOUND_EFFECT, 0.8f, 1.9f);
	public static final PlayerSound DWARF_MINE_DIAMOND = new RandomPitchSound("block.note_block.chime", Category.SOUND_EFFECT, 0.5f, 1f);
	public static final PlayerSound DWARF_MINE_EMERALD = new RandomPitchSound("block.note_block.harp", Category.SOUND_EFFECT, 0.5f, 1f);
	public static final PlayerSound DWARF_MINE_REDSTONE = new RandomPitchSound("block.note_block.bass", Category.SOUND_EFFECT, 0.5f, 1.5f);
	
	public static final GlobalSound MONSTER_DOOM_DRUM = new RandomPitchSound("drum", Category.MUSIC, 0.7f, 1f);
	public static final GlobalSound MONSTER_DOOM_DRUM_MANAMA = new StringGlobalSound("manamadrum", Category.MUSIC);
	
	public static final NoSound NO_SOUND = new NoSound();
}
