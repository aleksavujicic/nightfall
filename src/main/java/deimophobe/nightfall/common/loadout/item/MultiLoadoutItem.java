package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.Misc;

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
	
	private final Map<ConsumableType, Integer> consumables = new HashMap<>();
	private final Set<KitElementType> elements = new HashSet<>();
	
	public MultiLoadoutItem(ConfigurationSection config) {
		super(config);
		ConfigurationSection consumablesConfig = config.getConfigurationSection("consumables");
		for (String key : consumablesConfig.getKeys(false)) {
			ConsumableType consumable = ConsumableType.valueOf(key.toUpperCase());
			int quantity = consumablesConfig.getInt(key);
			consumables.put(consumable, quantity);
		}
		for (String item : config.getStringList("elements")) {
			elements.add(KitElementType.get(item));
		}
	}
	
	@Override
	public void modify(DwarfData dwarfData) {
		for (Map.Entry<ConsumableType, Integer> entry : consumables.entrySet())
			dwarfData.incrementConsumable(entry.getKey(), entry.getValue());
		
		for (KitElementType type : elements)
			dwarfData.addElement(type);
	}
}
