package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.blocks.blocktype.ComparableBlock;
import deimophobe.nightfall.blocks.blocktype.MaterialBlock;
import deimophobe.nightfall.blocks.blocktype.MaterialSet;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 19/03/17.
 */
public enum KitGiveType {
	START,
	
	SWORD(Material.LADDER),
	BOW(Material.DETECTOR_RAIL),
	ALE(new MaterialSet(Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON)),
	
	PICK(Material.ACTIVATOR_RAIL),
	AXE(Material.RAILS),
	SHOVEL(Material.POWERED_RAIL),
	
	ARTHEA_SPECIAL
	
	;
	
	private final ComparableBlock pickupBlock;
	
	KitGiveType() {
		this.pickupBlock = null;
	}
	
	KitGiveType(Material material) {
		this.pickupBlock = new MaterialBlock(material);
	}
	
	KitGiveType(ComparableBlock pickupBlock) {
		this.pickupBlock = pickupBlock;
	}
	
	private static final KitGiveType[] VALUES = values();
	public static KitGiveType getGiveTypeFromBlock(Block block) {
		for (KitGiveType giveType : VALUES) {
			if (giveType.pickupBlock == null) continue;
			
			if (giveType.pickupBlock.matchesBlock(block)) return giveType;
		}
		return null;
	}
}
