package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.game.entity.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

/**
 * Created by Deimophobe on 25/06/18.
 */
public class VineBlock extends DataTimedBlock {
	private final static BlockData VINE = Material.VINE.createBlockData();
	
	private final BlockFace facing;
	private final int extend;
	
	public VineBlock(int lifeTime, Block block, GameEntity placer, BlockFace facing, int extend) {
		super(lifeTime, block, placer, VINE);
		this.facing = facing;
		this.extend = extend;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (afterExactlyNTicks(10) && extend > 0) {
			Block newVineBlock = block.getRelative(BlockFace.UP);
			VineBlock vine = new VineBlock(getMaxLifetime(), newVineBlock, placer, facing, extend-1);
			BlockManager.getManager().placeTimedBlock(vine);
		}
	}
	
	@Override
	public boolean isPlaceable() {
		return NFBlocks.EMPTY_BLOCKS.matchesBlock(block)
				&& NFBlocks.SOLID.matchesBlock(block.getRelative(facing))
				&& GameMap.getCurrentMap().isBlockPlaceable(block)
				;
	}
	
	@Override
	protected void setBlock() {
		super.setBlock();
		
		Util.safeCastBlockData(block, MultipleFacing.class, vine -> {
			vine.setFace(facing, true);
		}, this::expire);
	}
	
	@Override
	public void placeBlock() {
		super.placeBlock();
		
		World world = block.getWorld();
		Location location = block.getLocation().add(0.5, 0.5, 0.5);
		world.playSound(location, "block.grass.place", 1f, 0.8f);
		world.spawnParticle(Particle.BLOCK_DUST, location, 5, 0.5, 0.5, 0.5, 0, VINE);
	}
	
	@Override
	public void unplaceBlock(boolean cancelled) {
		super.unplaceBlock(cancelled);
		if (cancelled) return;
		
		World world = block.getWorld();
		Location location = block.getLocation().add(0.5, 0.5, 0.5);
		world.playSound(location, "block.grass.break", 1f, 0.8f);
		world.spawnParticle(Particle.BLOCK_DUST, location, 15, 0.5, 0.5, 0.5, 0, VINE);
	}
	
	@Override
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {
		super.onHit(player, click, blockFace);
		if (click.isLeftClick() && player instanceof Dwarf) {
			this.expire();
		}
	}
}
