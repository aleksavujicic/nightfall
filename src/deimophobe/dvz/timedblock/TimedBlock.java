package deimophobe.dvz.timedblock;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 23/01/17.
 */
public abstract class TimedBlock {
	
	final Block block;
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
	
	
	private BukkitRunnable runnable;
	private Material oldType;
	private byte data;
	
	private void placeBlock() {
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
		runnable.runTaskLater(Game.getGame().getPlugin(), lifeTime);
		onPlace();
	}
	
	private void unPlaceBlock() {
		block.setType(oldType);
		block.setData(data);
		activeTimedBlocks.remove(block);
	}
	
	
	void onPlace() {}
	void onDestroy(boolean destroyed) {}
	public void onHit(GamePlayer player) {}
	
	
	public void cancel() {
		runnable.cancel();
		unPlaceBlock();
		onDestroy(true);
	}
	
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
	
	public static void hitBlock(Block block, GamePlayer gamePlayer) {
		TimedBlock tb = activeTimedBlocks.get(block);
		if (tb != null)
			tb.onHit(gamePlayer);
	}
	
}
