package deimophobe.dvz.menu.loadoutmenu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
class Loadout {
	
	private final Set<LoadoutItem> items = new HashSet<>();
	
	void selectItem(LoadoutItem item) {
		if (items.contains(item))
			items.remove(item);
		else
			items.add(item);
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
	
	private static final Map<Player, Loadout> loadouts = new HashMap<>();
	static Loadout getLoadout(Player player) {
		return loadouts.computeIfAbsent(player, k -> new Loadout());
	}
}
