package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.loadout.LoadoutConstruct;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class SimpleLoadoutItem extends LoadoutItem {
	private final String type;
	
	public SimpleLoadoutItem(ConfigurationSection config) {
		super(config);
		this.type = config.getString("name");
	}
	
	@Override
	public void modify(LoadoutConstruct construct) {
		construct.addElement(type);
	}
}
