package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class SettingsMenu extends SimpleMenu<PlayerSettings> implements MainMenu<PlayerSettings> {
	public SettingsMenu() {
		super(27);
		
		ConfigurationSection settingsConfig = NightfallCommonPlugin.getInternalFileConfig("settings.yml");
		ConfigurationSection toggleItem = settingsConfig.getConfigurationSection("hero-toggle.item");
		CustomItem item = CustomItem.getItem(toggleItem, "settings");
		ConfigurationSection toggleItem2 = settingsConfig.getConfigurationSection("mob-death.item");
		CustomItem item2 = CustomItem.getItem(toggleItem2, "settings");
		
		HeroToggleItem heroToggleItem = new HeroToggleItem(item);
		this.setItem(10, heroToggleItem);
		MobDeathToggleItem mobDeathMessageItem = new MobDeathToggleItem(item2);
		this.setItem(11, mobDeathMessageItem);
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
	public String getPermissionName() {
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
			return (settings.getValueOfSetting(Setting.HERO_ENABLED) ? enabled : disabled);
		}
		
		@Override
		public boolean onClick(MenuSession<PlayerSettings> session) {
			session.getData().toggleSetting(Setting.HERO_ENABLED);
			return true;
		}
	}
	
	private static final class MobDeathToggleItem implements MenuItem<PlayerSettings> {
		private final ItemStack enabled;
		private final ItemStack disabled;
		private MobDeathToggleItem(CustomItem item) {
			CustomItem enabled = item;
			CustomItem disabled = item.clone();
			
			enabled.setShiny(true);
			enabled.applyVariable("enabledtext", "Mob death messages &awill&r show in chat.");
			disabled.applyVariable("enabledtext", "Mob death messages &cwill not&r show in chat.");
			
			this.enabled = enabled.createItemStack();
			this.disabled = disabled.createItemStack();
		}
		
		@Override
		public ItemStack getDisplayItem(MenuSession<PlayerSettings> session) {
			PlayerSettings settings = session.getData();
			return (settings.getValueOfSetting(Setting.CHAT_MOB_DEATH_MESSAGES) ? enabled : disabled);
		}
		
		@Override
		public boolean onClick(MenuSession<PlayerSettings> session) {
			PlayerSettings settings = session.getData();
			settings.toggleSetting(Setting.CHAT_MOB_DEATH_MESSAGES);
			return true;
		}
	}
}
