package deimophobe.dvz.timedblock;

import deimophobe.dvz.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 23/01/17.
 */
public abstract class TimedBlock {
	
	private final Block block;
	private final Material newType;
	private final int lifeTime;
	
	public TimedBlock(Location loc, Material blockType, int lifeTime) {
		this(loc.getBlock(), blockType, lifeTime);
	}
	
	public TimedBlock(Block block, Material blockType, int lifeTime) {
		this.block = block;
		this.newType = blockType;
		this.lifeTime = lifeTime;
	}
	
	private void placeBlock() {
		final Material oldType = block.getType();
		final byte data = block.getData();
		
		block.setType(newType);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				block.setType(oldType);
				block.setData(data);
				activeTimedBlocks.remove(block);
				onDestroy();
			}
		}.runTaskLater(Game.getGame().getPlugin(), lifeTime);
	}
	
	protected abstract void onDestroy();
	
	private static final Map<Block, TimedBlock> activeTimedBlocks = new HashMap<>();
	public static boolean placeTimedBlock(TimedBlock timedBlock) {
		Block block = timedBlock.block;
		if (activeTimedBlocks.containsKey(block)) {
			return false;
		} else {
			timedBlock.placeBlock();
			activeTimedBlocks.put(block, timedBlock);
			return true;
		}
	}
	
}
