package deimophobe.nightfall.common.cosmetic.hat;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.cosmetic.Cosmetics;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SelectableItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Created by Deimophobe on 25/12/17.
 */
public class HatItem extends SelectableItem<Cosmetics> {
	private final Hat hat;
	
	public HatItem(ConfigurationSection config) throws InvalidConfigurationException {
		super(CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.HAT).createItemStack());
		
		Misc.checkConfigStringExists(config, "hat");
		this.hat = CosmeticManager.getManager().getHat(config.getString("hat"));
	}
	
	@Override
	public boolean onClick(MenuSession<Cosmetics> session) {
		if (isSelected(session)) {
			session.getData().setHat(null);
		} else {
			session.getData().setHat(hat);
		}
		return true;
	}
	
	@Override
	protected boolean isSelected(MenuSession<Cosmetics> session) {
		return (hat == session.getData().getHat());
	}
}