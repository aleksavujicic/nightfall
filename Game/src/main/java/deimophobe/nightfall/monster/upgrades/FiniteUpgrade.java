package deimophobe.nightfall.monster.upgrades;

import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 15/01/19.
 */
class FiniteUpgrade extends Upgrade {
	private final List<Integer> costs;
	private final Map<String, List<?>> values;
	private final Map<String, Object> defaults;
	private final List<ItemStack> items;
	
	FiniteUpgrade(String id, List<Integer> costs, Map<String, List<?>> values, Map<String, Object> defaults, Map<Upgrade, Integer> prerequisites, int index, CustomItem itemTemplate) {
		super(id, prerequisites, index);
		this.costs = costs;
		this.values = values;
		this.defaults = defaults;
		
		items = new ArrayList<>();
		for (int i=0; i<costs.size(); i++) {
			int cost = costs.get(i);
			
			itemTemplate.applyVariable("cost", "" + cost);
			for (Map.Entry<String, List<?>> entry : values.entrySet()) {
				String valueKey = entry.getKey();
				List<?> valueList = entry.getValue();
				Object value = valueList.get(i);
				itemTemplate.applyVariable(valueKey, "" + value);
			}
			
			ItemStack item = itemTemplate.createItemStack();
			item.setAmount(i + 1);
			items.add(item);
		}
	}
	
	@Override
	public ItemStack getItem(int level) {
		return items.get(level - 1);
	}
	
	@Override
	public Collection<String> getValueKeys() {
		return values.keySet();
	}
	
	@Override
	public Object getValue(String valueName, int level) {
		if (level == 0) return defaults.get(valueName);
		
		List<?> valueList  = values.get(valueName);
		if (valueList == null) return null;
		return valueList.get(level - 1);
	}
	
	@Override
	public int getMaxLevel() {
		return costs.size();
	}
	
	@Override
	public int getCost(int level) {
		return costs.get(level - 1);
	}
	
	@Override
	public boolean canUpgrade(int level) {
		return level < costs.size();
	}
}
