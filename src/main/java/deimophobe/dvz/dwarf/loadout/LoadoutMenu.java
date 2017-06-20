package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.Misc;
import deimophobe.dvz.items.lore.LoreTemplate;
import deimophobe.dvz.menu.*;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class LoadoutMenu extends CompositeMenu<Loadout> implements MainMenu<Loadout> {
	private static final LoadoutMenu menu = new LoadoutMenu(Misc.getInternalFileConfig("loadout.yml"));
	public static LoadoutMenu getMenu() {return menu;}
	
	public static void loadMenu() {
		menu.getTitle();
	}
	
	static final int PAGE_SIZE = 5*9;
	private static final int EXTRA_SIZE = 1*9;
	private static final String TITLE = "Select a kit";
	
	private static final String ITEM_SECTION = "specialitems";
	
	private LoadoutMenu(ConfigurationSection config) {
		// Setup menus
		List<LoadoutPage> tempPages = new ArrayList<>();
		for (String key : config.getKeys(false)) {
			if (!key.equals(ITEM_SECTION)) {
				tempPages.add(new LoadoutPage(config.getConfigurationSection(key)));
			}
		}
		MultiPageMenu<Loadout> pages = new MultiPageMenu<>(tempPages);
		SimpleMenu<Loadout> toolbar = new SimpleMenu<>(EXTRA_SIZE);
		
		addSubMenu(pages);
		addSubMenu(toolbar);
		
		// Add items for toolbar
		ConfigurationSection itemConfig = config.getConfigurationSection(ITEM_SECTION);
		ItemStack back = CustomItem.getItem(itemConfig.getConfigurationSection("back"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack forward = CustomItem.getItem(itemConfig.getConfigurationSection("forward"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack close = CustomItem.getItem(itemConfig.getConfigurationSection("close"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		ItemStack points = CustomItem.getItem(itemConfig.getConfigurationSection("points"), LoreTemplate.BASIC, Slot.MAIN_HAND).createItemStack();
		
		toolbar.setItem(0, new PointsItem(points, points));
		toolbar.setItem(3, new PageChanger<>(back, pages, false));
		toolbar.setItem(5, new PageChanger<>(forward, pages, true));
		toolbar.setItem(8, new CloseMenuItem<Loadout>(close));
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}
	
	@Override
	public Loadout getDataFromPlayer(Player player) {
		return Loadout.getLoadout(player);
	}
	
	private class PointsItem implements MenuItem<Loadout> {
		private final ItemStack pointsItem;
		private final ItemStack trashItem;
		
		PointsItem(ItemStack pointsItem, ItemStack trashItem) {
			this.pointsItem = pointsItem;
			this.trashItem = trashItem;
		}
		
		@Override
		public ItemStack getDisplayItem(MenuSession<Loadout> session) {
			int amt = session.getData().getRemainingPoints();
			
			if (amt == 0) {
				return trashItem;
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
}
