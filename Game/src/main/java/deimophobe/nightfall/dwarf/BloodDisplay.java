package deimophobe.nightfall.dwarf;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Created by Deimophobe on 13/10/18.
 */
public interface BloodDisplay {
	void showPrimaryBlood(Location center, int mana);
	void showSecondaryBlood(Location center, int mana);
	
	BloodDisplay DEFAULT = new ColouredBlood(Color.RED, Material.REDSTONE_BLOCK.createBlockData());
}
