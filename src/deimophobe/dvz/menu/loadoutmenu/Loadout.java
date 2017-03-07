package deimophobe.dvz.menu.loadoutmenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
class Loadout {
	
	private static final int MAX_POINTS = 64;
	
	private final Set<LoadoutItem> items = new HashSet<>();
	
	void selectItem(LoadoutItem item) {
		if (items.contains(item)) {
			items.remove(item);
		} else {
			Set<LoadoutItem> categoryItems = item.getItemsInCategory();
			categoryItems.retainAll(items);
			
			if (categoryItems.size() > 1) {
				// Should only ever be one item of the same category in a loadout
				Bukkit.getLogger().severe("Loadout contains more than one category item!? " + categoryItems.toString());
				items.remove(categoryItems);
			} else {
				int extraPoints = 0; // The amount of extra points one would get from removing a similar item.
				if (categoryItems.size() == 1) {
					LoadoutItem categoryItem = categoryItems.iterator().next();
					extraPoints += categoryItem.getCost();
				}
				
				// If there are still points after adding this item, let it be added
				if (getRemainingPoints() + extraPoints >= item.getCost()) {
					items.removeAll(categoryItems);
					items.add(item);
				}
			}
		}
	}
	
	boolean hasItem(LoadoutItem item) {
		return items.contains(item);
	}
	
	DwarfProperties constructProperties() {
		DwarfProperties prop = new DwarfProperties();
		for (LoadoutItem item : items) {
			item.modify(prop);
		}
		return prop;
	}
	
	int getRemainingPoints() {
		int usedPoints = 0;
		for (LoadoutItem item : items) {
			usedPoints += item.getCost();
		}
		return MAX_POINTS - usedPoints;
	}
	
	public void clear() {
		items.clear();
	}
	
	private static final Map<Player, Loadout> loadouts = new HashMap<>();
	static Loadout getLoadout(Player player) {
		return loadouts.computeIfAbsent(player, k -> new Loadout());
	}
	
}
