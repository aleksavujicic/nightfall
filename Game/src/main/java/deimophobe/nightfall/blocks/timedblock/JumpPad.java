package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 3/05/18.
 */
public class JumpPad extends DataTimedBlock {
	public JumpPad( Block block, GameEntity placer) {
		super(1, block, placer, Material.GOLD_PLATE);
	}
	
	@Override
	public void update() { }
	
	@Override
	public boolean isPlaceable() {
		return BlockType.EMPTY_BLOCKS.matchesBlock(block);
	}
	
	@Override
	public void unplaceBlock(boolean cancelled) {
		super.unplaceBlock(cancelled);
		if (cancelled) return;
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 15, 0.4, 0.4, 0.4, 0, block.getState().getData());
		world.playSound(center, Sound.BLOCK_STONE_PLACE, 1f, 1f);
	}
	
	@Override
	public void onHit(GamePlayer player, ClickType click, BlockFace blockFace) {
		super.onHit(player, click, blockFace);
		if (click.isLeftClick() && player instanceof MonsterPlayer) {
			this.expire();
		}
	}
	
	public void launchDwarf(Dwarf dwarf) {
		dwarf.leap(0.2, 1.25);
	}
}
