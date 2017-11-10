package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 21/06/17.
 */
public class NakedArmour extends StaticArmour {
	
	public NakedArmour(Dwarf dwarf) {
		dwarf.getPlayer().getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
	}
	
	@Override public double getResistance() { return 0; }
	@Override public int getManaRegenRate() { return 0; }
}
