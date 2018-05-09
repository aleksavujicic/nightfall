package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.common.loadout.NameTranslator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class MultiLoadoutItem extends LoadoutItem {
	
	private final Map<String, Integer> consumables = new LinkedHashMap<>();
	private final List<String> elements = new ArrayList<>();
	
	public MultiLoadoutItem(ConfigurationSection config) {
		super(config);
		ConfigurationSection consumablesConfig = config.getConfigurationSection("consumables");
		if (consumablesConfig != null) {
			for (String key : consumablesConfig.getKeys(false)) {
				String consumable = key.toLowerCase();
				int quantity = consumablesConfig.getInt(key);
				consumables.put(consumable, quantity);
			}
		}
		elements.addAll(config.getStringList("pieces"));
		
		addItemStringList();
	}
	
	@Override
	public void modify(Loadout loadout, LoadoutConstructable construct) {
		for (Map.Entry<String, Integer> entry : consumables.entrySet()) {
			tryIncrementConsumable(construct, entry.getKey(), entry.getValue());
		}
		
		for (String type : elements) {
			tryAddPiece(construct, type);
		}
	}
	
	private void addItemStringList() {
		CustomItem customItem = getItem();
		
		StringBuilder stringBuilder = new StringBuilder();
		NameTranslator nameTranslator = LoadoutManager.getManager().getNameTranslator();
		
		for (String item : elements) {
			String name = nameTranslator.getItemName(item);
			stringBuilder.append("* ").append(name).append('\n');
			
			if (name == null) customItem.addError("Unknown item: " + item);
		}
		
		for (Map.Entry<String, Integer> entry : consumables.entrySet()) {
			String name = nameTranslator.getConsumableName(entry.getKey(), entry.getValue());
			stringBuilder.append("* ").append(name).append('\n');
			
			if (name == null) customItem.addError("Unknown consumable: " + entry.getKey());
		}
		
		if (stringBuilder.length() > 0) {
			stringBuilder.setLength(stringBuilder.length() - 1);
		}
		
		String itemList = stringBuilder.toString();
		customItem.applyVariable("itemlist", itemList);
	}
}
