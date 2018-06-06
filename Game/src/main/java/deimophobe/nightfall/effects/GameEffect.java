package deimophobe.nightfall.effects;

import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.util.Colour;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class GameEffect {
	public static final PlayerEffectMaker DWARF_ARMOURED = new ArmourEffectMaker();
	public static final LocationEffectMaker DWARF_ARMOUR_CLOUD = new ArmourCloudMaker();
	
	public static final PlayerBlockEffectMaker SMALL_GOLD_MINE = new SmallGoldMineMaker(Colour.fromRGB(250, 250, 10), Sounds.DWARF_MINE_GOLD);
	public static final PlayerBlockEffectMaker LARGE_GOLD_MINE = new LargeGoldMineMaker(Colour.fromRGB(250, 250, 10), Sounds.DWARF_MINE_GOLD);
	public static final PlayerBlockEffectMaker DIAMOND_MINE = new SmallGoldMineMaker(Colour.fromRGB(104, 244, 255), Sounds.DWARF_MINE_DIAMOND);
	public static final PlayerBlockEffectMaker EMERALD_MINE = new SmallGoldMineMaker(Colour.fromRGB(35, 219, 68), Sounds.DWARF_MINE_EMERALD);
	public static final PlayerBlockEffectMaker REDSTONE_MINE = new FireGoldMineMaker(Sounds.DWARF_MINE_REDSTONE);
}
