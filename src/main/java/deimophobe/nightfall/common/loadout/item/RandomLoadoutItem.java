package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.loadout.Category;
import deimophobe.nightfall.common.loadout.DwarfData;
import deimophobe.nightfall.common.loadout.Loadout;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class RandomLoadoutItem extends LoadoutItem {
	
	protected RandomLoadoutItem(ConfigurationSection config) {
		super(config, Category.KIT, Loadout.MAX_POINTS);
	}
	
	@Override
	public void modify(DwarfData dwarfData) {
		int pointsRemaining = Loadout.MAX_POINTS;
		for (Category category : Category.values()) {
			if (category == Category.KIT) continue;
			
			LoadoutItem item = Misc.getRandom(category.getItems());
			pointsRemaining -= item.getCost();
			item.modify(dwarfData);
		}
		
		Set<LoadoutItem> remaining = new HashSet<>();
		remaining.addAll(Category.ACCESSORY.getItems());
		remaining.addAll(Category.CONSUMABLE.getItems());
		while (pointsRemaining >= 0) {
			LoadoutItem item = Misc.getRandom(remaining);
			pointsRemaining -= item.getCost();
			item.modify(dwarfData);
			remaining.remove(item);
		}
	}
}
