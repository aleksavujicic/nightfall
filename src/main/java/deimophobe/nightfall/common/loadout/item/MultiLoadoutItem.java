package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.loadout.DwarfData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class MultiLoadoutItem extends LoadoutItem {
	
	private final Map<String, Integer> consumables = new HashMap<>();
	private final Set<String> elements = new HashSet<>();
	
	public MultiLoadoutItem(ConfigurationSection config) {
		super(config);
		ConfigurationSection consumablesConfig = config.getConfigurationSection("consumables");
		for (String key : consumablesConfig.getKeys(false)) {
			String consumable = key.toLowerCase();
			int quantity = consumablesConfig.getInt(key);
			consumables.put(consumable, quantity);
		}
		elements.addAll(config.getStringList("elements"));
	}
	
	@Override
	public void modify(DwarfData dwarfData) {
		for (Map.Entry<String, Integer> entry : consumables.entrySet())
			dwarfData.incrementConsumable(entry.getKey(), entry.getValue());
		
		for (String type : elements)
			dwarfData.addElement(type);
	}
}
