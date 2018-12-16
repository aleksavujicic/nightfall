package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class FurnaceConsumable extends Consumable {
	private final ConsumableType result;
	private final int time;
	
	public FurnaceConsumable(String name, ConsumableType result, int time) {
		super(name);
		this.result = result;
		this.time = time;
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!click.isRightClick()) return ConsumeResult.FAILURE;
		if (!NFBlocks.FURNACE.matchesBlock(clickedBlock)) return ConsumeResult.FAILURE;
		
		int count;
		if (dwarf.isSneaking()) {
			count = dwarf.getItemCount(this);
			dwarf.removeAllItems(this);
		} else {
			count = dwarf.getHeldItemCount();
			dwarf.useHeldItemStack();
		}
		
		dwarf.getFurnace().addItems(result, time, count);
		dwarf.playSound("entity.generic.burn", 1f, 0.8f, true);
		
		Location center = clickedBlock.getLocation().add(0.5, 0.5, 0.5);
		center.getWorld().spawnParticle(Particle.FLAME, center, 35, 0.5, 0.5, 0.5, 0);
		center.getWorld().spawnParticle(Particle.SMOKE_NORMAL, center, 35, 0.5, 0.5, 0.5, 0);
		
		return ConsumeResult.SUCCESS;
	}
}
