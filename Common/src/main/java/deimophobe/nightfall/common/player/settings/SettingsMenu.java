package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class SettingsMenu extends SimpleMenu<PlayerSettings> implements MainMenu<PlayerSettings> {
	public SettingsMenu() {
		super(27);
		
		ConfigurationSection settingsConfig = NightfallCommonPlugin.getInternalFileConfig("settings.yml");
		ConfigurationSection toggleItem = settingsConfig.getConfigurationSection("hero-toggle.item");
		CustomItem item = CustomItem.getItem(toggleItem, "settings");
		
		HeroToggleItem heroToggleItem = new HeroToggleItem(item);
		this.setItem(10, heroToggleItem);
	}
	
	@Override
	public String getTitle() {
		return "Settings";
	}
	
	@Override
	public PlayerSettings getDataFromPlayer(Player player) {
		return PlayerManager.getManager().getSettings(player);
	}
	
	@Override
	public String getMenuPermission() {
		return "settings";
	}
	
	private static final class HeroToggleItem implements MenuItem<PlayerSettings> {
		private final ItemStack enabled;
		private final ItemStack disabled;
		private HeroToggleItem(CustomItem item) {
			CustomItem enabled = item;
			CustomItem disabled = item.clone();
			
			enabled.setShiny(true);
			enabled.applyVariable("enabledtext", "You &amay&r become a hero.");
			disabled.applyVariable("enabledtext", "You &cwill not&r be a hero");
			
			this.enabled = enabled.createItemStack();
			this.disabled = disabled.createItemStack();
		}
		
		@Override
		public ItemStack getDisplayItem(MenuSession<PlayerSettings> session) {
			PlayerSettings settings = session.getData();
			return (settings.isHeroEnabled() ? enabled : disabled);
		}
		
		@Override
		public boolean onClick(MenuSession<PlayerSettings> session) {
			session.getData().toggleHero();
			return true;
		}
	}
}
