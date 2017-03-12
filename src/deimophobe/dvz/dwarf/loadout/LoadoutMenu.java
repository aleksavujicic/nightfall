package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.menu.Menu;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class LoadoutMenu implements Menu<Player> {
	private static final LoadoutMenu menu;
	public static LoadoutMenu getMenu() {return menu;}
	static {
		ConfigurationSection loadoutData = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("loadout.yml"));
		menu = new LoadoutMenu(loadoutData);
	}
	
	private final List<LoadoutPage> pages = new ArrayList<>();
	private final Map<Player, Integer> pageNumber = new HashMap<>();
	
	static final int PAGE_SIZE = 5*9;
	private static final int EXTRA_SIZE = 1*9;
	private static final String TITLE = "Select a kit";
	
	private static final String ITEM_SECTION = "specialitems";
	private final ItemStack back, forward, clear, points;
	
	private LoadoutMenu(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {
			if (!key.equals(ITEM_SECTION)) {
				pages.add(new LoadoutPage(config.getConfigurationSection(key)));
			}
		}
		ConfigurationSection itemConfig = config.getConfigurationSection(ITEM_SECTION);
		back = ItemCreator.createItem(itemConfig.getConfigurationSection("back"), Slot.MAIN_HAND);
		forward = ItemCreator.createItem(itemConfig.getConfigurationSection("forward"), Slot.MAIN_HAND);
		clear = ItemCreator.createItem(itemConfig.getConfigurationSection("clear"), Slot.MAIN_HAND);
		points = ItemCreator.createItem(itemConfig.getConfigurationSection("points"), Slot.MAIN_HAND);
	}
	
	
	@Override
	public Inventory getInventory(Player player) {
		Inventory pageInv = getPageForPlayer(player).getInventory(player);
		Inventory newInv = Bukkit.createInventory(pageInv.getHolder(), PAGE_SIZE + EXTRA_SIZE, TITLE);
		
		newInv.setContents(pageInv.getContents());
		
		int remainPoints = Loadout.getLoadout(player).getRemainingPoints();
		ItemStack newPoints = points.clone();
		newPoints.setAmount(remainPoints);
		
		// Back and forward buttons
		newInv.setItem(PAGE_SIZE+3, back);
		newInv.setItem(PAGE_SIZE+5, forward);
		
		newInv.setItem(PAGE_SIZE+0, newPoints);
		newInv.setItem(PAGE_SIZE+8, clear);
																							
		return newInv;
	}
	
	@Override
	public void select(int i, Player player) {
		if (i < PAGE_SIZE) {
			getPageForPlayer(player).select(i, player);
		} else {
			switch (i - PAGE_SIZE) {
				case 0: // Points
					break;
				case 3: // Back
					changePage(player, -1);
					showTo(player);
					break;
				case 5: // Forward
					changePage(player, 1);
					showTo(player);
					break;
				case 8: // Clear
					Loadout.getLoadout(player).clear();
					showTo(player);
					break;
			}
		}
			
	}
	
	@Override
	public void showTo(Player player) {
		player.openInventory(getInventory(player));
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}
	
	private int getPageNumberForPlayer(Player player) {
		return pageNumber.computeIfAbsent(player, p -> 0);
	}
	
	private LoadoutPage getPageForPlayer(Player player) {
		return pages.get(getPageNumberForPlayer(player));
	}
	
	private void changePage(Player player, int i) {
		int newI = (getPageNumberForPlayer(player) + i + pages.size()) % pages.size();
		pageNumber.put(player, newI);
	}
}
