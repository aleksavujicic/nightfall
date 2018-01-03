package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.Misc;
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
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class LoadoutMenu extends CompositeMenu<Loadout> implements MainMenu<Loadout> {
	private static final LoadoutMenu menu = new LoadoutMenu();
	public static LoadoutMenu getMenu() {return menu;}
	
	static final int PAGE_SIZE = 5*9;
	private static final int EXTRA_SIZE = 1*9;
	private static final String TITLE = "Select a kit";
	
	private LoadoutMenu() {
		// Setup menus
		List<LoadoutPage> tempPages = new ArrayList<>();
		tempPages.add(new LoadoutPage(Misc.getInternalFileConfig("common/loadout/page-classes.yml")));
		tempPages.add(new LoadoutPage(Misc.getInternalFileConfig("common/loadout/page-weapons.yml")));
		tempPages.add(new LoadoutPage(Misc.getInternalFileConfig("common/loadout/page-accessory.yml")));
		
		MultiPageMenu<Loadout> pages = new MultiPageMenu<Loadout>(tempPages);
		SimpleMenu<Loadout> toolbar = new SimpleMenu<>(EXTRA_SIZE);
		
		addSubMenu(pages);
		addSubMenu(toolbar);
		
		// Add items for toolbar
		ConfigurationSection itemConfig = Misc.getInternalFileConfig("common/loadout/special-items.yml");
		ItemStack back = CustomItem.getItem(itemConfig.getConfigurationSection("back"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack forward = CustomItem.getItem(itemConfig.getConfigurationSection("forward"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack close = CustomItem.getItem(itemConfig.getConfigurationSection("close"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack points = CustomItem.getItem(itemConfig.getConfigurationSection("points"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack trash = CustomItem.getItem(itemConfig.getConfigurationSection("trash"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		
		toolbar.setItem(0, new PointsItem(points, points));
		toolbar.setItem(1, new ClearItem(trash));
		toolbar.setItem(3, new PageChanger<>(back, pages, false));
		toolbar.setItem(5, new PageChanger<>(forward, pages, true));
		toolbar.setItem(8, new CloseMenuItem<>(close));
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}
	
	@Override
	public Loadout getDataFromPlayer(Player player) {
		return LoadoutManager.getManager().getLoadout(player);
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
