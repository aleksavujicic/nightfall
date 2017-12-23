package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.loadout.Category;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutConstruct;
import deimophobe.nightfall.common.loadout.LoadoutManager;
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
	public void modify(LoadoutConstruct construct) {
		int pointsRemaining = Loadout.MAX_POINTS;
		for (Category category : Category.values()) {
			if (category == Category.KIT) continue;
			
			LoadoutItem item = Misc.getRandom(category.getItems());
			pointsRemaining -= item.getCost();
			item.modify(construct);
		}
		
		Set<LoadoutItem> remaining = new HashSet<>();
		remaining.addAll(Category.ACCESSORY.getItems());
		remaining.addAll(Category.CONSUMABLE.getItems());
		remaining.remove(LoadoutManager.getManager().getItem("untimely"));
		while (pointsRemaining >= 0) {
			LoadoutItem item = Misc.getRandom(remaining);
			pointsRemaining -= item.getCost();
			item.modify(construct);
			remaining.remove(item);
		}
	}
}
