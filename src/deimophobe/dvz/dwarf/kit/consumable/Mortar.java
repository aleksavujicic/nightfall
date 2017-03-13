package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.Game;
import deimophobe.dvz.blocks.BlockManager;
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
	
	
	@Override
	public boolean use(Dwarf dwarf) {
		Block block = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		
		boolean used = BlockManager.getManager().mortarWalls(block, wizzy);
		
		if (used)
			dwarf.playSound("mortar", 1, (float) (0.6 + 0.1*Math.random() + (wizzy ? 0.2 : 0)), false);
		
		return used;
	}
}
