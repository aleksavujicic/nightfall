package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Slab extends Consumable {
	
	Slab(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) return FAILED_CD;
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		if (clickedBlock == null)
			clickedBlock = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		
		if (clickedBlock.getType() != Material.AIR) return FAILED_CD;
			
		Location center = clickedBlock.getLocation();
		double facing = dwarf.getLocation().getYaw() % 360;
		if (facing < 0)
			facing += 360;
		World world = center.getWorld();
		
		int x_center = center.getBlockX();
		int y_center = center.getBlockY();
		int z_center = center.getBlockZ();
		
		int x_size = 1; // actual size is double this plus 1 (so 3)
		int y_size = 2; // actual size 5
		int z_size = 5; // actual size 11
		if ((135 < facing && facing <= 215) || facing > 315 || facing <= 45) {
			x_size = 5;
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
		
		dwarf.playSound("block.anvil.place", 20, 0.8f, false);
		
		return DEFAULT_CD;
	}
}
