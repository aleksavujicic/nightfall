package deimophobe.nightfall.common.player.cosmetic.title;

import deimophobe.nightfall.common.ConfigValidator;
import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.player.cosmetic.Cosmetics;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SelectableItem;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class TitleItem extends SelectableItem<Cosmetics> {
	private final String title;
	
	public TitleItem(ConfigurationSection config) throws MalformedConfigurationException {
		super(CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.TITLE).createItemStack());
		
		ConfigValidator.checkChildExists(config, "title");
		this.title = config.getString("title");
	}

	@Override
	public boolean onClick(MenuSession<Cosmetics> session) {
		if (isSelected(session)) {
			session.getData().setTitle(null);
		} else {
			session.getData().setTitle(title);
		}
		return true;
	}
	
	@Override
	protected boolean isSelected(MenuSession<Cosmetics> session) {
		return title.equalsIgnoreCase(session.getData().getTitle());
	}
}
