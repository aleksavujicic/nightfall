package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.blocks.blocktype.BlockMatcher;
import deimophobe.nightfall.blocks.NFBlocks;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 19/03/17.
 */
public enum PickupType {
	START,
	
	SWORD(NFBlocks.PICKUP_SWORD, "dwarf.pickup.sword"),
	BOW(NFBlocks.PICKUP_BOW, "dwarf.pickup.bow"),
	ALE(NFBlocks.PICKUP_ALE, "dwarf.pickup.ale") {
		@Override
		public void playPickupSound(Location location) {
			location.getWorld().playSound(location, "entity.generic.drink", 1f, 0.9f);
		}
	},
	
	PICK(NFBlocks.PICKUP_PICK, "dwarf.pickup.pick"),
	AXE(NFBlocks.PICKUP_AXE, "dwarf.pickup.axe"),
	SHOVEL(NFBlocks.PICKUP_SHOVEL, "dwarf.pickup.shovel"),
	
	ARTHEA_SPECIAL
	
	;
	
	private final BlockMatcher pickupBlock;
	private final String pickupSound;
	
	PickupType() {
		this.pickupBlock = null;
		this.pickupSound = null;
	}
	
	PickupType(BlockMatcher pickupBlock, String pickupSound) {
		this.pickupBlock = pickupBlock;
		this.pickupSound = pickupSound;
	}
	
	
	public void playPickupSound(Location location) {
		if (pickupSound == null) return;
		location.getWorld().playSound(location, pickupSound, 1f, 1f);
	}
	
	
	private static final PickupType[] VALUES = values();
	public static PickupType getGiveTypeFromBlock(Block block) {
		for (PickupType giveType : VALUES) {
			if (giveType.pickupBlock == null) continue;
			
			if (giveType.pickupBlock.matchesBlock(block)) return giveType;
		}
		return null;
	}
}
