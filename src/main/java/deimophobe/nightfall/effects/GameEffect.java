package deimophobe.nightfall.effects;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class GameEffect {
	public static final PlayerEffectMaker DWARF_ARMOURED = new ArmourEffectMaker();
	public static final LocationEffectMaker DWARF_ARMOUR_CLOUD = new ArmourCloudMaker();
	
	public static final PlayerBlockEffectMaker SMALL_GOLD_MINE = new SmallGoldMineMaker();
	public static final PlayerBlockEffectMaker LARGE_GOLD_MINE = new LargeGoldMineMaker();
}
