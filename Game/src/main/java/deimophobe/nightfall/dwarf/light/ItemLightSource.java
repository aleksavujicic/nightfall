package deimophobe.nightfall.dwarf.light;

import deimophobe.nightfall.common.items.ItemMatcher;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 8/12/18.
 */
public class ItemLightSource implements LightSource {
	private final ItemMatcher matcher;
	private final int visionGain;
	
	public ItemLightSource(ItemMatcher matcher, int visionGain) {
		this.matcher = matcher;
		this.visionGain = visionGain;
	}
	
	@Override
	public void apply(Dwarf dwarf, Vision vision) {
		ItemStack item = dwarf.getHeldItem();
		if (matcher.doesItemMatch(item)) {
			vision.increaseVision(visionGain);
		}
	}
}
