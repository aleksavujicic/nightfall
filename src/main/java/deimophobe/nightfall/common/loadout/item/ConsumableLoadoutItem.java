package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.Misc;

import deimophobe.nightfall.common.loadout.Category;
import deimophobe.nightfall.common.loadout.DwarfData;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class ConsumableLoadoutItem extends LoadoutItem {
	private final ConsumableType type;
	private final int quantity;
	
	public ConsumableLoadoutItem(ConfigurationSection config) {
		super(config, Category.CONSUMABLE);
		
		this.type = ConsumableType.valueOf(config.getString("name").toUpperCase());
		this.quantity = config.getInt("quantity");
		
		getItem().applyVariable("quantity", ""+quantity);
	}
	
	@Override
	public void modify(DwarfData dwarfData) {
		dwarfData.incrementConsumable(type, quantity);
	}
}
