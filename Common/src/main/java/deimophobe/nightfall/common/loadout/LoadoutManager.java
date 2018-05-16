package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.loadout.item.LoadoutItem;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class LoadoutManager {
	private static LoadoutManager ourManager = new LoadoutManager();
	public static LoadoutManager getManager() { return ourManager; }
	
	private final Map<String, LoadoutItem> items = new HashMap<>();
	
	private final NameTranslator nameTranslator = new NameTranslator();
	
	public NameTranslator getNameTranslator() { return nameTranslator; }
	
	private LoadoutManager() {
		ourManager = this;
	}
	
	public int registerLoadoutItem(LoadoutItem item, String id) {
		checkNotNull(id, "Loadout ID cannot be null.");
		checkNotNull(item, "LoadoutItem cannot be null.");
		
		id = id.toLowerCase().replace('_','-');
		checkArgument(!items.containsKey(id), "Cannot register loadout item '" + id + "' as item with id already exists. IDs must be unique.");
		
		items.put(id, item);
		return items.size();
	}
	
	public LoadoutItem getItem(String id) {
		checkArgument(items.containsKey(id), "No loadout item with ID '" + id + "' exists.");
		return items.get(id);
	}
	
	public LoadoutItem getDefaultKit() {
		return getItem("warrior-class");
	}
	
	/** Useful for checking malformed loadout items */
	public void modifyAll(LoadoutConstructable constructable) {
		Loadout loadout = new Loadout();
		for (LoadoutItem item : items.values()) {
			item.modify(loadout, constructable);
		}
		for (Category category : Category.values()) {
			category.giveDefault(constructable);
		}
	}
}
