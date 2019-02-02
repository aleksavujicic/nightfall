package deimophobe.nightfall.dwarf;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Created by Deimophobe on 12/10/18.
 */
public enum BloodColour {
	RED(BloodDisplay.DEFAULT),
	BLUE(26, 218, 255, Material.LIGHT_BLUE_WOOL.createBlockData()),
	GREEN(154, 255, 26, Material.LIME_WOOL.createBlockData()),
	
	;
	
	private final BloodDisplay display;
	
	BloodColour(BloodDisplay display) {
		this.display = display;
	}
	
	BloodColour(int red, int green, int blue, BlockData secondaryBlockData) {
		this.display = new ColouredBlood(
				Color.fromRGB(red, green, blue),
				secondaryBlockData
		);
	}
	
	void showPrimaryBlood(Location center, int mana) {
		display.showPrimaryBlood(center, mana);
	}
	void showSecondaryBlood(Location center, int mana) {
		display.showSecondaryBlood(center, mana);
	}
}
