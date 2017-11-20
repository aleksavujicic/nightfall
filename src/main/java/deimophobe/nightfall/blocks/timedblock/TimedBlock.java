package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 23/01/17.
 */
public class TimedBlock {
	
	protected final Block block;
	private final Material newType;
	private final int lifeTime;
	private final GameEntity placer;
	private boolean active = false;
	
	
	public TimedBlock(Location loc, Material blockType, int lifeTime, GameEntity placer) {
		this(loc.getBlock(), blockType, lifeTime, placer);
	}
	
	public TimedBlock(Block block, Material blockType, int lifeTime, GameEntity placer) {
		this.block = block;
		this.newType = blockType;
		this.lifeTime = lifeTime;
		this.placer = placer;
	}
	
	
	private BukkitRunnable runnable;
	private Material oldType;
	private byte data;
	
	private void placeBlock() {
		active = true;
		oldType = block.getType();
		data = block.getData();
		
		block.setType(newType);
		
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				unPlaceBlock();
				onDestroy(false);
			}
		};
		runnable.runTaskLater(NightfallPlugin.getPlugin(), lifeTime);
		onPlace();
	}
	
	private void unPlaceBlock() {
		active = false;
		block.setType(oldType);
		block.setData(data);
		activeTimedBlocks.remove(block);
	}
	
	
	void onPlace() {}
	void onDestroy(boolean cancelled) {}
	void onHit(GamePlayer player) {}
	
	public boolean isActive() {
		return active;
	}
	
	
	public void cancel() {
		runnable.cancel();
		unPlaceBlock();
		onDestroy(true);
	}
	
	private static final Map<Block, TimedBlock> activeTimedBlocks = new HashMap<>();
	public static boolean placeTimedBlock(TimedBlock timedBlock) {
		Block block = timedBlock.block;
		
		// If its already a timed block or its not breakable, don't overwrite!
		if (timedBlock instanceof GoboBox) {
			if (BlockType.EMPTY_BLOCKS.matchesBlock(block)) {
				block.setType(Material.AIR);
				timedBlock.placeBlock();
				activeTimedBlocks.put(block, timedBlock);
				return true;
			}
			return false;
		}
		else if (activeTimedBlocks.containsKey(block) || BlockType.UNTIMEABLE_BLOCKS.matchesBlock(block)) {
			return false;
		} else {
			timedBlock.placeBlock();
			activeTimedBlocks.put(block, timedBlock);
			return true;
		}
	}
	
	public static void hitBlock(Block block, GamePlayer gamePlayer) {
		TimedBlock tb = activeTimedBlocks.get(block);
		if (tb != null)
			tb.onHit(gamePlayer);
	}
	
	public static void cancelAllBlocks() {
		Set<TimedBlock> copy = new HashSet<>(activeTimedBlocks.values());
		for (TimedBlock tb : copy) {
			tb.cancel();
		}
	}

	public GameEntity getPlacer() {
		return placer;
	}
}
