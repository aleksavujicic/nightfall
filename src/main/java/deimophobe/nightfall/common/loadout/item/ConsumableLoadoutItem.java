package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.loadout.Category;
import deimophobe.nightfall.common.loadout.DwarfData;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class ConsumableLoadoutItem extends LoadoutItem {
	private final String consumable;
	private final int quantity;
	
	public ConsumableLoadoutItem(ConfigurationSection config) {
		super(config, Category.CONSUMABLE);
		
		this.consumable = config.getString("name").toLowerCase();
		this.quantity = config.getInt("quantity");
		
		getItem().applyVariable("quantity", ""+quantity);
	}
	
	@Override
	public void modify(DwarfData dwarfData) {
		dwarfData.incrementConsumable(consumable, quantity);
	}
}
