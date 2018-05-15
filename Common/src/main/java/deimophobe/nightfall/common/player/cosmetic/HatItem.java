package deimophobe.nightfall.common.player.cosmetic;

import deimophobe.nightfall.common.ConfigValidator;
import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SelectableItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 25/12/17.
 */
class HatItem extends SelectableItem<Cosmetics> {
	@Nullable
	private final Hat hat;
	
	static HatItem fromConfig(ConfigurationSection config) throws MalformedConfigurationException {
		ConfigValidator.checkChildExists(config, "hat");
		
		ItemStack hatItem = CustomItem.getItem(config.getConfigurationSection("hat"), LoreTemplate.HAT).createItemStack();
		String name = config.getName();
		Hat hat = HatStore.getStore().createHat(name, hatItem);
		
		ItemStack menuItem;
		if (config.contains("item")) {
			menuItem = CustomItem.getItem(config.getConfigurationSection("item"), LoreTemplate.HAT).createItemStack();
		} else {
			menuItem = hatItem;
		}
		return new HatItem(hat, menuItem);
	}
	
	private HatItem(@Nullable Hat hat, @NotNull ItemStack item) {
		super(item);
		this.hat = hat;
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