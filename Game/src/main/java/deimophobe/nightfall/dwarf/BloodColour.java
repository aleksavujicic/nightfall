package deimophobe.nightfall.dwarf;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

/**
 * Created by Deimophobe on 12/10/18.
 */
public enum BloodColour {
	RED(BloodDisplay.DEFAULT),
	BLUE(0.1, 0.85, 1, new Wool(DyeColor.LIGHT_BLUE)),
	GREEN(0.6, 1, 0.1, new Wool(DyeColor.LIME)),
	
	;
	
	private final BloodDisplay display;
	
	BloodColour(BloodDisplay display) {
		this.display = display;
	}
	
	BloodColour(double r, double g, double b, MaterialData secondaryBlockData) {
		this.display = new ColouredBlood(r, g, b, secondaryBlockData);
	}
	
	void showPrimaryBlood(Location center, int mana) {
		display.showPrimaryBlood(center, mana);
	}
	void showSecondaryBlood(Location center, int mana) {
		display.showSecondaryBlood(center, mana);
	}
}
