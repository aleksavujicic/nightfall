package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.loadout.save.SaveLoadoutMenu;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.item.CloseMenuItem;
import deimophobe.nightfall.common.menu.item.MenuOpenerItem;
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
		// Setup pages
		List<LoadoutPage> loadoutPages = new ArrayList<>();
		loadoutPages.add(getPage("loadout/page-classes.yml"));
		loadoutPages.add(getPage("loadout/page-weapons.yml"));
		loadoutPages.add(getPage("loadout/page-accessory.yml"));
		loadoutPages.add(getPage("loadout/page-consumable.yml"));
		
		MultiPageMenu<Loadout> pages = new MultiPageMenu<Loadout>(loadoutPages);
		addSubMenu(pages);
		
		
		// Add items for toolbar
		SimpleMenu<Loadout> toolbar = new SimpleMenu<>(EXTRA_SIZE);
		
		ItemStack back    = getSpecialItem("back");
		ItemStack forward = getSpecialItem("forward");
		ItemStack close   = getSpecialItem("close");
		ItemStack points  = getSpecialItem("points");
		ItemStack trash   = getSpecialItem("trash");
		ItemStack random  = getSpecialItem("random");
		ItemStack save    = getSpecialItem("loadsave");
		SaveLoadoutMenu saveMenu = new SaveLoadoutMenu();
		
		toolbar.setItem(0, new PointsMenuItem(points, points));
		toolbar.setItem(1, new ClearLoadoutItem(trash));
		toolbar.setItem(2, new RandomMenuItem(random));
		toolbar.setItem(3, new PageChanger<>(back, pages, false));
		toolbar.setItem(5, new PageChanger<>(forward, pages, true));
		toolbar.setItem(6, new MenuOpenerItem<>(save, saveMenu));
		toolbar.setItem(8, new CloseMenuItem<>(close));
		
		addSubMenu(toolbar);
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
	
	@Override
	public String getMenuPermission() {
		return "loadout";
	}
	
}
