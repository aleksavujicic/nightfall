package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Slab extends Consumable {
	
	Slab(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return FAILED_CD;
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		Block selectedBlock = dwarf.getTargetBlock(null, 7);
		Location center = selectedBlock.getLocation();
		if (dwarf.distanceTo(center) <= 4) {
			dwarf.sendTitleMessage(ChatColor.RED + "That block is too close to slab");
			return FAILED_CD;
		}
		
		double facing = dwarf.getLocation().getYaw() % 360;
		if (facing < 0)
			facing += 360;
		World world = center.getWorld();
		
		int x_center = center.getBlockX();
		int y_center = center.getBlockY();
		int z_center = center.getBlockZ();
		
		int x_size = 1; // actual size is double this plus 1 (so 3)
		int y_size = 2; // actual size 5
		int z_size = 6; // actual size 13
		if ((135 < facing && facing <= 215) || facing > 315 || facing <= 45) {
			x_size = 6;
			z_size = 1;
		}
		
		GameMap map = GameMap.getCurrentMap();
		for (int x = x_center - x_size; x <= x_center + x_size; x++) {
			for (int y = y_center - y_size+1; y <= y_center + y_size+1; y++) {
				for (int z = z_center - z_size; z <= z_center + z_size; z++) {
					Block toReplace = world.getBlockAt(x,y,z);
					if (map.isBlockPlaceable(toReplace) && BlockType.SLABBABLE.matchesBlock(toReplace)) {
						toReplace.setType(Material.LAPIS_ORE);
						world.spawnParticle(Particle.CLOUD, x+0.5,y+0.5,z+0.5, 4, 0.5,0.5,0.5, 0.1);
					}
				}
			}
		}
		
		dwarf.playSound("block.anvil.place", 1, 0.8f, true);
		
		return DEFAULT_CD;
	}
}
