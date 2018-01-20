package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.loadout.Category;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
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
	public void modify(LoadoutConstructable construct) {
		int pointsRemaining = Loadout.MAX_POINTS;
		Set<LoadoutItem> remaining = new HashSet<>();
		
		// Go through categories, apply ones with single items
		for (Category category : Category.values()) {
			if (category == Category.KIT) continue;
			
			if (category.isSingleItem()) {
				LoadoutItem item = Misc.getRandom(category.getItems());
				pointsRemaining -= item.getCost();
				item.modify(construct);
			} else {
				remaining.addAll(category.getItems());
			}
		}
		
		// Randomly choose from remaing items
		while (pointsRemaining >= 0) {
			LoadoutItem item = Misc.getRandom(remaining);
			if (item == null) break;
			
			if (item.isRandomSelectable()) {
				pointsRemaining -= item.getCost();
				item.modify(construct);
			}
			
			remaining.remove(item);
		}
	}
}
