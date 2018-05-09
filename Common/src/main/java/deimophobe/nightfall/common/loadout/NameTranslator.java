package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 9/05/18.
 */
public class NameTranslator {
	private final Map<String, String> itemTranslator = new HashMap<>();
	private final Map<String, ConsumableName> consumableTranslator = new HashMap<>();
	
	
	NameTranslator() {
		ConfigurationSection translations = NightfallCommonPlugin.getInternalFileConfig("loadout/name-translations.yml");
		
		ConfigurationSection items = translations.getConfigurationSection("items");
		ConfigurationSection consumables = translations.getConfigurationSection("consumables");
		
		for (String key : items.getKeys(false)) {
			String name = items.getString(key);
			itemTranslator.put(key, name);
		}
		
		for (String key : consumables.getKeys(false)) {
			String name = consumables.getString(key);
			ConsumableName consumableName = new ConsumableName(name);
			consumableTranslator.put(key, consumableName);
		}
	}
	
	public String getItemName(String item) {
		if (item == null) throw new IllegalArgumentException("Item must not be null.");
		
		return itemTranslator.get(item);
	}
	
	public String getConsumableName(String consumable, int amount) {
		if (consumable == null) throw new IllegalArgumentException("Consumable must not be null.");
		
		ConsumableName consumableName = consumableTranslator.get(consumable);
		if (consumableName == null) return null;
		
		if (amount == 1) {
			return amount + " " + consumableName.singular;
		} else {
			return amount + " " + consumableName.plural;
		}
	}
	
	private static class ConsumableName {
		private final String singular;
		private final String plural;
		
		public ConsumableName(String string) {
			String[] names = string.split("\\|");
			
			singular = names[0];
			if (names.length == 1) {
				plural = names[0];
			} else {
				plural = names[1];
			}
		}
	}
}
