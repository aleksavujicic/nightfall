package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.player.GamePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 23/01/17.
 */
public abstract class TimedBlock extends LifetimeExpireable {
	
	protected final Block block;
	protected final GameEntity placer;
	private boolean active = false;
	
	public Block getBlock() { return block; }
	public GameEntity getPlacer() { return placer; }
	public boolean isActive() {
		return active;
	}
	
	public TimedBlock(int lifeTime, Block block, GameEntity placer) {
		super(lifeTime);
		this.block = block;
		this.placer = placer;
	}
	
	@Override
	public void onExpiry() {
		super.onExpiry();
		
		// Could be already unplaced by a cancel() call.
		if (active) unplaceBlock(false);
	}
	
	public abstract boolean isPlaceable();
	
	public void placeBlock() {
		active = true;
		setBlock();
	}
	
	public void unplaceBlock(boolean cancel) {
		active = false;
		unsetBlock();
		BlockManager.getManager().removeTimedBlock(this);
	}
	
	public void cancel() {
		expire();
		unplaceBlock(true);
	}
	
	protected abstract void setBlock();
	protected abstract void unsetBlock();
	
	
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {}
	
	
	public boolean matchesBlock(Block other) {
		return block.getX() == other.getX()
				&& block.getY() == other.getY()
				&& block.getZ() == other.getZ();
	}

}
