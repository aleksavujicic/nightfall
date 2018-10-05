package deimophobe.nightfall.blocks;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.Manager;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 25/04/18.
 */
public class BlockManager implements Manager {
	public static BlockManager getManager() { return Game.getGame().getManager(BlockManager.class); }
	private final BlockListener listener;
	
	public BlockManager(Game game) {
		listener = new BlockListener(this);
		game.addGameListener(listener);
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void stop() {
		cancelAllTimedBlocks();
	}
	
	
	public boolean breakBlock(Block block) {
		if (!GameMap.getCurrentMap().isBlockBreakable(block)) return false;
		
		World world = block.getWorld();
		Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
		world.spawnParticle(Particle.BLOCK_CRACK, blockCenter, 50, 0.5, 0.5, 0.5, 0, block.getState().getData());
		NMSUtil.playBlockBreakSound(block);
		block.breakNaturally();
		return true;
	}
	
	
	// ------ TIMED BLOCKS ------
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
	
	public TimedBlock getTimedBlock(Block block) {
		return activeTimedBlocks.get(block);
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
