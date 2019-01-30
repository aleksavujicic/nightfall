package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.jetbrains.annotations.NotNull;

import static deimophobe.nightfall.util.NFConditions.checkMaterialIsBlock;

/**
 * Created by Deimophobe on 16/12/18.
 */
public class ArmourBlockInteracter implements BlockInteracter {
	private static final BlockFace[] VALID_DIRECTIONS = new BlockFace[]{
			BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
	};
	
	@NotNull private final BlockInteracter normalInteracter;
	
	public ArmourBlockInteracter(Material material) {
		checkMaterialIsBlock(material);
		this.normalInteracter = new MaterialBlock(material);
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		if (!normalInteracter.matchesBlock(block)) return false;
		
		for (BlockFace direction : VALID_DIRECTIONS) {
			if (checkDirection(block, direction)) return true;
		}
		
		return false;
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		normalInteracter.setAtBlock(block);
	}
	
	private boolean checkDirection(Block block, BlockFace direction) {
		Block baseBlock = block.getRelative(direction);
		Material type = baseBlock.getType();
		BlockData data = baseBlock.getBlockData();
		
		BlockFace facingDirection = direction.getOppositeFace();
		
		if (type != Material.PISTON && type != Material.PISTON_HEAD) return false;
		
		if (data instanceof Piston) {
			Piston piston = (Piston) data;
			return !piston.isExtended() && piston.getFacing() == facingDirection;
		}
		else if (data instanceof PistonHead) {
			PistonHead pistonHead = ((PistonHead) data);
			return pistonHead.getType() == TechnicalPiston.Type.NORMAL && pistonHead.getFacing() == facingDirection;
		}
		
		return false;
	}
}
