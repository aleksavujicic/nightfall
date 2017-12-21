package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.Misc;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import deimophobe.nightfall.dwarf.kit.elements.KitElementType;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
public class Loadout implements SessionData {
	
	public static final int MAX_POINTS = 64;
	
	private final SortedSet<LoadoutItem> items = new TreeSet<>();
	private final Map<Category, LoadoutItem> categoryItems = new HashMap<>();
	
	private final UUID playerUUID;
	
	Loadout(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	
	public boolean selectItem(LoadoutItem item) {
		Category cat = item.getCategory();
		
		if (items.contains(item)) {
			items.remove(item);
			if (cat != null) categoryItems.remove(cat);
			
			updateDisplay();
			return true;
		} else {
			// The amount of extra points one would get from removing a similar item.
			int extraPoints = 0;
			LoadoutItem categoryItem = categoryItems.get(cat);
			if (categoryItem != null) {
				extraPoints = categoryItem.getCost();
			}
			
			// If there are still points after adding this item, let it be added
			if (getRemainingPoints() + extraPoints >= item.getCost()) {
				if (categoryItem != null) items.remove(categoryItem);
				
				items.add(item);
				if (cat.isSingleItem())
					categoryItems.put(cat, item);
				
				updateDisplay();
				return true;
			}
			return false;
		}
	}
	
	public boolean hasItem(LoadoutItem item) {
		return items.contains(item);
	}
	
	public int getRemainingPoints() {
		int usedPoints = 0;
		for (LoadoutItem item : items) {
			usedPoints += item.getCost();
		}
		return MAX_POINTS - usedPoints;
	}
	
	public void clear() {
		items.clear();
		categoryItems.clear();
		
		updateDisplay();
	}
	
	private void updateDisplay() { // TODO move this elsewhere maybe? Make lobbyer class maybe?
//		Player player = Bukkit.getPlayer(playerUUID);
//		if (player != null && Game.getGame().isLobbyPlayer(player)) {
//			PlayerInventory inv = player.getInventory();
//			inv.clear();
//			int i = 9;
//			for (LoadoutItem item : items) {
//				ItemStack itemStack = item.getItemStack().clone();
//				itemStack.setAmount(1);
//				inv.setItem(i, itemStack);
//				i++;
//			}
//		}
	}
	
	public DwarfData constructProperties() {
		DwarfData data = new DwarfData();
		boolean hasKit = false;
		for (LoadoutItem item : items) {
			item.modify(data);
			if (item.getCategory() == Category.KIT)
				hasKit = true;
		}
		
		// Apply warrior class if kit is empty
		if (getRemainingPoints() == MAX_POINTS) {
			LoadoutManager.getManager().getDefaultKit().modify(data);
			hasKit = true;
		}
		
		// Add defaults if missing
		if (!hasKit) {
			for (Category category : Category.values()) {
				if (!categoryItems.containsKey(category)) {
					KitElementType defaultElement = category.getDefault();
					if (defaultElement != null)
						data.addElement(defaultElement);
				}
			}
		}
		return data;
	}
}
