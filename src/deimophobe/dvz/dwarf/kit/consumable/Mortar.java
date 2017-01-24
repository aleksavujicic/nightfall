package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Mortar extends Consumable {
	private final boolean wizzy;
	
	Mortar(ItemStack item, boolean wizzy) {
		super(item);
		this.wizzy = wizzy;
	}
	
	private static final double BLUE_WALL_MORTAR_CHANCE = 0.03;
	
	@Override
	public boolean use(Dwarf dwarf) {
		Block block = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		
		Location center = block.getLocation();
		World world = center.getWorld();
		
		int x_center = center.getBlockX();
		int y_center = center.getBlockY();
		int z_center = center.getBlockZ();
		
		final int x_size = 4; // actual size is double this plus 1 (so 9)
		final int y_size = 3; // actual size 7
		final int z_size = 4; // actual size 9
		
		boolean used = false;
		for (int x = x_center - x_size; x <= x_center + x_size; x++) {
			for (int y = y_center - y_size; y <= y_center + y_size; y++) {
				for (int z = z_center - z_size; z <= z_center + z_size; z++) {
					Block toReplace = world.getBlockAt(x,y,z);
					if (isWallBlock(toReplace.getType())) {
						if (wizzy || Game.getGame().getPhase().canBlueWalls() || Math.random() <= BLUE_WALL_MORTAR_CHANCE) {
							toReplace.setType(Material.LAPIS_ORE);
						} else {
							toReplace.setType(Material.SMOOTH_BRICK);
						}
						used = true;
					}
				}
			}
		}
		
		if (used)
			dwarf.playSound("block.anvil.place", 20, 0.8f, false);
		
		return used;
	}
	
	// TODO move to a more sensible spot after wall breaking mechanics impl.
	private final static Material[] WALL_BLOCKS = {
			//Material.LAPIS_ORE,
			Material.SMOOTH_BRICK,
			Material.COBBLESTONE
	};
	private static boolean isWallBlock(Material material) {
		for (Material wallBlock : WALL_BLOCKS) {
			if (wallBlock == material)
				return true;
		}
		return false;
	}
}
