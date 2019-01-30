package deimophobe.nightfall.blocks;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.Manager;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.common.util.NMSUtil;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 25/04/18.
 */
public class BlockManager implements Manager {
	public static BlockManager getManager() { return Game.getGame().getManager(BlockManager.class); }
	
	private final Game game;
	private final BlockListener listener;
	
	// Because torches break in stupidly many ways :(
	private final BukkitRunnable torchChecker;
	
	private final Map<Block, TimedBlock> activeTimedBlocks = new HashMap<>();
	private final Map<Block, UUID> placedTorches = new HashMap<>();
	
	public BlockManager(Game game) {
		this.game = game;
		this.listener = new BlockListener(this);
		
		this.torchChecker = new BukkitRunnable() {
			@Override
			public void run() {
				verifyTorches();
			}
		};
	}
	
	@Override
	public void init() {
		game.addGameListener(listener);
		torchChecker.runTaskTimer(game.getPlugin(), 5*60*20, 5*60*20);
	}
	
	@Override
	public void stop() {
		game.removeGameListener(listener);
		torchChecker.cancel();
		cancelAllTimedBlocks();
	}
	
	Game getGame() {
		return game;
	}
	
	
	public boolean breakBlock(Block block) {
		if (!GameMap.getCurrentMap().isBlockBreakable(block)) return false;
		
		checkTorchBreaking(block);
		
		World world = block.getWorld();
		Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
		world.spawnParticle(Particle.BLOCK_CRACK, blockCenter, 50, 0.5, 0.5, 0.5, 0, block.getBlockData());
		NMSUtil.playBlockBreakSound(block);
		block.breakNaturally();
		
		return true;
	}
	
	
	// ------ TIMED BLOCKS ------
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
	
	
	// ------ TORCHES ------
	
	void placeTorch(Block torch, Player placer) {
		checkArgument(NFBlocks.TORCH.matchesBlock(torch), "Block %s must be a torch.", torch);
		
		UUID uuid = placer.getUniqueId();
		placedTorches.put(torch, uuid);
	}
	
	private static final Set<BlockFace> POSSIBLE_ADJACENT_TORCHES = EnumSet.of(
			BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
	);
	void checkTorchBreaking(Block block) {
		placedTorches.remove(block);
		
		// Check torch above broken block
		Block above = block.getRelative(BlockFace.UP);
		if (above.getType() == Material.TORCH) placedTorches.remove(above);
		
		// Check torches to side, remove if sticking in block
		for (BlockFace adjacentFace : POSSIBLE_ADJACENT_TORCHES) {
			Block adjacentBlock = block.getRelative(adjacentFace);
			
			if (adjacentBlock.getType() == Material.WALL_TORCH) {
				Directional torch = (Directional) adjacentBlock.getBlockData();
				BlockFace facing = torch.getFacing();
				if (facing == adjacentFace) {
					placedTorches.remove(adjacentBlock);
				}
			}
		}
	}
	
	public boolean isValidExperienceTorch(Block torch, Player player) {
		UUID placer = placedTorches.get(torch);
		if (placer == null) return false;
		
		UUID playerUUID = player.getUniqueId();
		return !placer.equals(playerUUID);
	}
	
	public boolean verifyTorches() {
		return placedTorches.entrySet().removeIf(entry -> {
			Block block = entry.getKey();
			return !NFBlocks.TORCH.matchesBlock(block);
		});
	}
	
	public int getTorchCount(Player player) {
		UUID uuid = player.getUniqueId();
		int count = 0;
		
		for (UUID placer : placedTorches.values()) {
			if (uuid.equals(placer)) {
				count++;
			}
		}
		
		return count;
	}
}
