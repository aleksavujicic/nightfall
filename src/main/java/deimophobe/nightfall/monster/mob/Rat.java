package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Rat extends AbstractMob {
	
	Rat(MonsterPlayer monster) {
		super(monster, MobType.RAT);
	}
	
	private int stealCD = 0;
	private static final int STEAL_MAX_CD = 5;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (stealCD > 0)
			stealCD--;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (stealCD == 0 && Misc.isRightClick(action) && clickedBlock != null) {
			if (BlockType.ACTIVE_SHRINE_BLOCK.matchesBlock(clickedBlock) && GameMap.getCurrentMap().hasGold()) {
				monster.playSound("coin", 1f, 1f, true);
				monster.gainXP(1, false);
				GameMap.getCurrentMap().stealGold(3);
				stealCD = STEAL_MAX_CD;
			}
		}
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block.getType() == Material.TORCH)
			monster.playSound("entity.silverfish.ambient", 1f, 1f, true);
	}
}
