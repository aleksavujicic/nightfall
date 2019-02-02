package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/19.
 */
class SettingMenuItem implements MenuItem<PlayerSettings> {
	private final Setting<Boolean, ?> setting;
	private final ItemStack enabledItem;
	private final ItemStack disabledItem;
	
	SettingMenuItem(SettingsMenuConfig.ItemConfig itemConfig) {
		this.setting = itemConfig.getSetting();
		
		CustomItem enabledTemplate = itemConfig.getTemplate().clone();
		CustomItem disabledTemplate = itemConfig.getTemplate().clone();
		
		enabledTemplate.applyVariable("enabled", ChatColor.GREEN + "enabled");
		disabledTemplate.applyVariable("enabled", ChatColor.RED + "disabled");
		
		enabledTemplate.applyVariable("description", itemConfig.getEnabledText());
		disabledTemplate.applyVariable("description", itemConfig.getDisabledText());
		
		enabledTemplate.setShiny(true);
		
		this.enabledItem = enabledTemplate.createItemStack();
		this.disabledItem = disabledTemplate.createItemStack();
	}
	
	
	@Override
	public ItemStack getDisplayItem(MenuSession<PlayerSettings> session) {
		PlayerSettings settings = session.getData();
		return (settings.getValueOfSetting(setting) ? enabledItem : disabledItem);
	}
	
	@Override
	public boolean onClick(MenuSession<PlayerSettings> session) {
		session.getData().toggleSetting(setting);
		return true;
	}
}
