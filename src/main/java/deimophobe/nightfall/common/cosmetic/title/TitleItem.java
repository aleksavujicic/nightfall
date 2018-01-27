package deimophobe.nightfall.common.cosmetic.title;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.cosmetic.Cosmetic;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SelectableItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class TitleItem extends SelectableItem<Cosmetic> {
	private final String title;
	
	public TitleItem(ConfigurationSection config) throws InvalidConfigurationException {
		super(CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.TITLE).createItemStack());
		
		Misc.checkConfigStringExists(config, "title");
		this.title = config.getString("title");
	}

	@Override
	public boolean onClick(MenuSession<Cosmetic> session) {
		if (isSelected(session)) {
			session.getData().setTitle(null);
		} else {
			session.getData().setTitle(title);
		}
		return true;
	}
	
	@Override
	protected boolean isSelected(MenuSession<Cosmetic> session) {
		return title.equalsIgnoreCase(session.getData().getTitle());
	}
}
