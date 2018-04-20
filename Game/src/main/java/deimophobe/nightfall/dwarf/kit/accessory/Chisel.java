package deimophobe.nightfall.dwarf.kit.accessory;

import com.google.common.collect.Lists;
import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.CustomBlock;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Deimophobe on 16/01/18.
 */
public class Chisel extends AbstractItem {
	private final ComplexCooldown chiseller = new ComplexCooldown(2);
	
	public Chisel(Dwarf dwarf) { super(dwarf); }
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "chisel");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	@Override
	public void update() {
		super.update();
		chiseller.update();
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && chiseller.isAvailable() && chiselBlock(clickedBlock)) {
			chiseller.reset();
			return true;
		}
		return false;
	}
	
	
	private static final List<CustomBlock> CHISEL_BLOCKS = Lists.newArrayList(
			new Cobble(),
			new CobbleStair(BlockFace.NORTH, false),
			new CobbleStair(BlockFace.EAST, false),
			new CobbleStair(BlockFace.SOUTH, false),
			new CobbleStair(BlockFace.WEST, false),
			new CobbleStair(BlockFace.NORTH, true),
			new CobbleStair(BlockFace.EAST, true),
			new CobbleStair(BlockFace.SOUTH, true),
			new CobbleStair(BlockFace.WEST, true),
			new CobbleSlab(false),
			new CobbleSlab(true),
			new Cobble()
	);
	private static boolean chiselBlock(Block block) {
		Iterator<CustomBlock> iterator = CHISEL_BLOCKS.iterator();
		while (iterator.hasNext()) {
			CustomBlock cBlock = iterator.next();
			if (cBlock.matchesBlock(block)) {
				CustomBlock newBlock = iterator.next();
				newBlock.setAtBlock(block);
				return true;
			}
		}
		return false;
	}
	
	
	private static class CobbleStair implements CustomBlock {
		
		private final BlockFace facing;
		private final boolean inverted;
		
		private CobbleStair(BlockFace facing, boolean inverted) {
			this.facing = facing;
			this.inverted = inverted;
		}
		
		@Override
		public boolean matchesBlock(Block block) {
			if (block.getType() == Material.COBBLESTONE_STAIRS) {
				MaterialData data = block.getState().getData();
				if (data instanceof Stairs) {
					Stairs stairs = (Stairs) data;
					return (stairs.getAscendingDirection() == facing && stairs.isInverted() == inverted);
				}
			}
			return false;
		}
		
		@Override
		public void setAtBlock(Block block) {
			block.setType(Material.COBBLESTONE_STAIRS);
			
			BlockState state = block.getState();
			Stairs data = (Stairs) state.getData();
			
			data.setFacingDirection(facing);
			data.setInverted(inverted);
			
			state.setData(data);
			state.update();
		}
	}
	
	private static class CobbleSlab implements CustomBlock {
		
		private final boolean inverted;
		
		private CobbleSlab(boolean inverted) {
			this.inverted = inverted;
		}
		
		@Override
		public void setAtBlock(Block block) {
			block.setType(Material.STEP);
			
			BlockState state = block.getState();
			Step data = (Step) state.getData();
			
			data.setMaterial(Material.COBBLESTONE);
			data.setInverted(inverted);
			
			state.setData(data);
			state.update();
		}
		
		@Override
		public boolean matchesBlock(Block block) {
			if (block.getType() == Material.STEP) {
				MaterialData data = block.getState().getData();
				if (data instanceof Step) {
					Step step = (Step) data;
					return (step.getMaterial() == Material.COBBLESTONE && step.isInverted() == inverted);
				}
			}
			return false;
		}
	}
	
	private static class Cobble implements CustomBlock {
		
		@Override
		public void setAtBlock(Block block) {
			block.setType(Material.COBBLESTONE);
		}
		
		@Override
		public boolean matchesBlock(Block block) {
			return block.getType() == Material.COBBLESTONE;
		}
	}
}
