package deimophobe.dvz.monster.mob;

import deimophobe.dvz.blocks.timedblock.GoboBox;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 28/02/17.
 */
class Goblin extends Mob {
	protected Goblin(MonsterPlayer mons) {
		super(mons, MobType.GOBO);
	}
	
	@Override
	public void update() {
		if (placeBoxCD > 0)
			placeBoxCD--;
	}
	
	private static final int MAX_PLACE_CD = 10;
	private int placeBoxCD = 0;
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (DwarvenItem.isRightClick(action) && isPlayerHoldingItem(1) && placeBoxCD == 0 && clickedBlock != null) {
			Block block = clickedBlock.getRelative(blockFace);
			TimedBlock.placeTimedBlock(new GoboBox(block, 50, 5));
			monster.useHeldItem();
			placeBoxCD = MAX_PLACE_CD;
		}
	}
	
}
