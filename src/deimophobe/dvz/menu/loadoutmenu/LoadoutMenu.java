package deimophobe.dvz.menu.loadoutmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.menu.Menu;
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
	static final int EXTRA_SIZE = 1*9;
	private static final String TITLE = "Select a kit";
	
	private LoadoutMenu(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {
			pages.add(new LoadoutPage(config.getConfigurationSection(key)));
		}
	}
	
	@Override
	public Inventory getInventory(Player player) {
		Inventory pageInv = getPageForPlayer(player).getInventory(player);
		Inventory newInv = Bukkit.createInventory(pageInv.getHolder(), PAGE_SIZE + EXTRA_SIZE, TITLE);
		
		newInv.setContents(pageInv.getContents());
		
		// Back and forward buttons
		newInv.setItem(PAGE_SIZE+3, new ItemStack(Material.FLINT_AND_STEEL));
		newInv.setItem(PAGE_SIZE+5, new ItemStack(Material.DIAMOND_SWORD));
		
		newInv.setItem(PAGE_SIZE+0, new ItemStack(Material.INK_SACK, 1, (short) 2));
		newInv.setItem(PAGE_SIZE+8, new ItemStack(Material.ENDER_STONE, 1));
																							
		return newInv;
	}
	
	@Override
	public void select(int i, Player player) {
		if (i < PAGE_SIZE) {
			getPageForPlayer(player).select(i, player);
		} else {
			switch (i - PAGE_SIZE) {
				case 0: // Points
					Bukkit.broadcastMessage("POINTS");
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
					Bukkit.broadcastMessage("CLEAR");
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
		return pageNumber.computeIfAbsent(player, p -> new Integer(0));
	}
	
	private LoadoutPage getPageForPlayer(Player player) {
		return pages.get(getPageNumberForPlayer(player));
	}
	
	private void changePage(Player player, int i) {
		int newI = (getPageNumberForPlayer(player) + i + pages.size()) % pages.size();
		pageNumber.put(player, newI);
	}
}
