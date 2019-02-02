package deimophobe.nightfall.monster.upgrades;

import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Deimophobe on 15/01/19.
 */
class InfiniteUpgrade extends Upgrade {
	private final int cost;
	private final Map<String, Object> values;
	private final ItemStack itemStack;
	
	InfiniteUpgrade(String id, int cost, Map<String, Object> values, Map<Upgrade,Integer> prerequisites, int index, CustomItem itemTemplate) {
		super(id, prerequisites, index);
		this.cost = cost;
		this.values = values;
		
		itemTemplate.applyVariable("cost", "" + cost);
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String valueKey = entry.getKey();
			Object value = entry.getValue();
			itemTemplate.applyVariable(valueKey, "" + value);
		}
		itemStack = itemTemplate.createItemStack();
	}
	
	@Override
	public ItemStack getItem(int level) {
		ItemStack copy = itemStack.clone();
		copy.setAmount(level);
		return copy;
	}
	
	@Override
	public Collection<String> getValueKeys() {
		return values.keySet();
	}
	
	@Override
	public Object getValue(String valueName, int level) {
		return values.get(valueName);
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public int getCost(int level) {
		return cost;
	}
	
	@Override
	public boolean canUpgrade(int level) {
		return true;
	}
}
