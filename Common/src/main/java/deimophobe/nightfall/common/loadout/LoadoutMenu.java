package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.CloseMenuItem;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.menu.item.PageChanger;
import deimophobe.nightfall.common.menu.submenu.CompositeMenu;
import deimophobe.nightfall.common.menu.submenu.MultiPageMenu;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class LoadoutMenu extends CompositeMenu<Loadout> implements MainMenu<Loadout> {
	static final int PAGE_SIZE = 5*9;
	private static final int EXTRA_SIZE = 1*9;
	private static final String TITLE = "Select a kit";
	
	public LoadoutMenu() {
		// Setup menus
		List<LoadoutPage> tempPages = new ArrayList<>();
		tempPages.add(getPage("loadout/page-classes.yml"));
		tempPages.add(getPage("loadout/page-weapons.yml"));
		tempPages.add(getPage("loadout/page-accessory.yml"));
		tempPages.add(getPage("loadout/page-consumable.yml"));
		
		MultiPageMenu<Loadout> pages = new MultiPageMenu<Loadout>(tempPages);
		SimpleMenu<Loadout> toolbar = new SimpleMenu<>(EXTRA_SIZE);
		
		addSubMenu(pages);
		addSubMenu(toolbar);
		
		// Add items for toolbar
		ItemStack back    = getSpecialItem("back");
		ItemStack forward = getSpecialItem("forward");
		ItemStack close   = getSpecialItem("close");
		ItemStack points  = getSpecialItem("points");
		ItemStack trash   = getSpecialItem("trash");
		ItemStack random  = getSpecialItem("random");
		
		toolbar.setItem(0, new PointsItem(points, points));
		toolbar.setItem(1, new ClearItem(trash));
		toolbar.setItem(2, new RandomMenuItem(random));
		toolbar.setItem(3, new PageChanger<>(back, pages, false));
		toolbar.setItem(5, new PageChanger<>(forward, pages, true));
		toolbar.setItem(8, new CloseMenuItem<>(close));
	}
	
	private LoadoutPage getPage(String name) {
		return new LoadoutPage(NightfallCommonPlugin.getInternalFileConfig(name));
	}
	
	private ItemStack getSpecialItem(String name) {
		ConfigurationSection itemConfig = NightfallCommonPlugin.getInternalFileConfig("loadout/special-items.yml");
		return CustomItem.getItem(itemConfig.getConfigurationSection(name), LoreTemplate.BASIC).createItemStack();
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}
	
	@Override
	public Loadout getDataFromPlayer(Player player) {
		return PlayerManager.getManager().getLoadout(player);
	}
	
	private class PointsItem implements MenuItem<Loadout> {
		private final ItemStack pointsItem;
		private final ItemStack emptyItem;
		
		PointsItem(ItemStack pointsItem, ItemStack emptyItem) {
			this.pointsItem = pointsItem;
			this.emptyItem = emptyItem;
		}
		
		@Override
		public ItemStack getDisplayItem(MenuSession<Loadout> session) {
			int amt = session.getData().getRemainingPoints();
			
			if (amt == 0) {
				return emptyItem;
			} else {
				ItemStack item = pointsItem.clone();
				item.setAmount(amt);
				return item;
			}
		}
		
		@Override
		public boolean onClick(MenuSession<Loadout> session) {
			session.getData().clear();
			return true;
		}
	}
	
	private class ClearItem implements MenuItem<Loadout> {
		private final ItemStack item;
		
		ClearItem(ItemStack item) {
			this.item = item;
		}
		
		@Override
		public ItemStack getDisplayItem(MenuSession<Loadout> session) {
			return item;
		}
		
		@Override
		public boolean onClick(MenuSession<Loadout> session) {
			session.getData().clear();
			return true;
		}
		
	}
}
