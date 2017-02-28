package deimophobe.dvz.monster.mob;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Rat extends Mob {
	Rat(MonsterPlayer monster) {
		super(monster, MobType.RAT);
	}
	
	private int stealCD = 0;
	private static final int STEAL_MAX_CD = 5;
	
	@Override
	public void update() {
		if (stealCD > 0)
			stealCD--;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (stealCD == 0 && DwarvenItem.isRightClick(action)) {
			Block block = monster.getPlayer().getTargetBlock((Set<Material>) null, 4);
			Game game = Game.getGame();
			if (block.getType() == Material.ENDER_PORTAL_FRAME && game.getShrine().getShrineRegion().containsBlock(block)) {
				monster.playSound("entity.experience_orb.pickup");
				game.stealGold(1);
				stealCD = STEAL_MAX_CD;
			}
		}
	}
}
