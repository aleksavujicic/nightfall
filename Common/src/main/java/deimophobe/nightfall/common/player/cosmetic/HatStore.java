package deimophobe.nightfall.common.player.cosmetic;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 15/05/18.
 */
public class HatStore {
	private static final HatStore store = new HatStore();
	public static HatStore getStore() { return store; }
	
	
	private final Map<String, Hat> hats;
	private HatStore() {
		hats = new HashMap<>();
	}
	
	public Hat createHat(String name, ItemStack hatItem) {
		checkNotNull(name, "Name must not be null.");
		checkNotNull(hatItem, "Hat must not be null.");
		checkArgument(!hats.containsKey(name), "Name must not be an existing hat name (got '%s').", name);
		
		Hat hat = new Hat(name, hatItem);
		hats.put(name, hat);
		return hat;
	}
	public Hat getHat(String name) {
		if (name == null) return null;
		
		checkArgument(hats.containsKey(name), "Name must be a valid hat name (got '%s').", name);
		
		return hats.get(name.toLowerCase());
	}
	
}
