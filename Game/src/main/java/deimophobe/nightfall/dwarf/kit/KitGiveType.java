package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.blocks.blocktype.ComparableBlock;
import deimophobe.nightfall.blocks.blocktype.MaterialBlock;
import deimophobe.nightfall.blocks.blocktype.MaterialSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 19/03/17.
 */
public enum KitGiveType {
	START,
	
	SWORD(Material.LADDER, "dwarf.pickup.sword"),
	BOW(Material.DETECTOR_RAIL, "dwarf.pickup.bow"),
	ALE(new MaterialSet(Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON), "dwarf.pickup.ale") {
		@Override
		public void playPickupSound(Location location) {
			location.getWorld().playSound(location, "entity.generic.drink", 1f, 0.9f);
		}
	},
	
	PICK(Material.ACTIVATOR_RAIL, "dwarf.pickup.pick"),
	AXE(Material.RAILS, "dwarf.pickup.axe"),
	SHOVEL(Material.POWERED_RAIL, "dwarf.pickup.shovel"),
	
	ARTHEA_SPECIAL
	
	;
	
	private final ComparableBlock pickupBlock;
	private final String pickupSound;
	
	KitGiveType() {
		this.pickupBlock = null;
		this.pickupSound = null;
	}
	
	KitGiveType(Material material, String pickupSound) {
		this.pickupBlock = new MaterialBlock(material);
		this.pickupSound = pickupSound;
	}
	
	KitGiveType(ComparableBlock pickupBlock, String pickupSound) {
		this.pickupBlock = pickupBlock;
		this.pickupSound = pickupSound;
	}
	
	
	public void playPickupSound(Location location) {
		if (pickupSound == null) return;
		location.getWorld().playSound(location, pickupSound, 1f, 1f);
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
