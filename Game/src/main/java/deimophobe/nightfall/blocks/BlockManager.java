package deimophobe.nightfall.blocks;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GamePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 25/04/18.
 */
public class BlockManager {
	public static BlockManager getManager() { return Game.getGame().getBlockManager(); }
	
	public void stop() {
		cancelAllTimedBlocks();
	}
	
	private final Map<Block, TimedBlock> activeTimedBlocks = new HashMap<>();
	public boolean placeTimedBlock(TimedBlock timedBlock) {
		Block block = timedBlock.getBlock();
		
		// If its already a timed block don't overwrite
		if (activeTimedBlocks.containsKey(block)) return false;
		
		// If not placeable, don't overwrite!
		if (!timedBlock.isPlaceable()) return false;
		
		
		timedBlock.placeBlock();
		activeTimedBlocks.put(block, timedBlock);
		Game.getGame().addUpdateable(timedBlock);
		return true;
	}
	
	public void hitBlock(Block block, GamePlayer gamePlayer, ClickType click, BlockFace blockFace) {
		TimedBlock timedBlock = activeTimedBlocks.get(block);
		if (timedBlock != null) {
			timedBlock.onHit(gamePlayer, click, blockFace);
		}
	}
	
	public boolean isTimedBlock(Block block) {
		return activeTimedBlocks.containsKey(block);
	}
	
	public <T extends TimedBlock> Set<T> getTimedBlocks(Class<T> tClass) {
		Set<T> timedBlocks = new HashSet<>();
		for (TimedBlock timedBlock : activeTimedBlocks.values()) {
			if (tClass.isInstance(timedBlock)) {
				timedBlocks.add(tClass.cast(timedBlock));
			}
		}
		return timedBlocks;
	}
	
	public void removeTimedBlock(TimedBlock timedBlock) {
		activeTimedBlocks.remove(timedBlock.getBlock());
	}
	
	public void cancelAllTimedBlocks() {
		Set<TimedBlock> copy = new HashSet<>(activeTimedBlocks.values());
		for (TimedBlock timedBlock : copy) {
			timedBlock.cancel();
		}
	}
}
