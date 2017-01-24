package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Slab extends Consumable {
	
	Slab(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean use(Dwarf dwarf) {
		Block block = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		
		if (block.getType() != Material.AIR) return false;
			
		Location center = block.getLocation();
		double facing = dwarf.getLocation().getYaw() % 360;
		if (facing < 0)
			facing += 360;
		World world = center.getWorld();
		
		int x_center = center.getBlockX();
		int y_center = center.getBlockY();
		int z_center = center.getBlockZ();
		
		int x_size = 1; // actual size is double this plus 1 (so 3)
		int y_size = 2; // actual size 5
		int z_size = 4; // actual size 9
		if ((135 < facing && facing <= 215) || facing > 315 || facing <= 45) {
			x_size = 4;
			z_size = 1;
		}
		
		for (int x = x_center - x_size; x <= x_center + x_size; x++) {
			for (int y = y_center - y_size+1; y <= y_center + y_size+1; y++) {
				for (int z = z_center - z_size; z <= z_center + z_size; z++) {
					Block toReplace = world.getBlockAt(x,y,z);
					if (toReplace.getType() == Material.AIR) {
						toReplace.setType(Material.LAPIS_ORE);
					}
				}
			}
		}
		
		dwarf.playSound("block.anvil.place", 20, 0.8f, false);
		
		return true;
	}
}
