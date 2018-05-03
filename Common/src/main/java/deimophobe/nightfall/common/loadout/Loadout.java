package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import deimophobe.nightfall.common.menu.SessionData;

import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
public class Loadout implements SessionData {
	
	private static final String UNTIMELY_DEMISE_NAME = "untimely";
	private static final String RANDOM_NAME = "random-class";
	
	public static final int MAX_POINTS = 64;
	
	private final SortedSet<LoadoutItem> items = new TreeSet<>();
	private final Map<Category, LoadoutItem> categoryItems = new HashMap<>();
	
	public boolean selectItem(LoadoutItem item) {
		Category cat = item.getCategory();
		
		if (items.contains(item)) {
			items.remove(item);
			if (cat != null) categoryItems.remove(cat);
			
			updateDisplay();
			return true;
		} else {
			// The amount of extra points one would get from removing a similar item.
			Set<LoadoutItem> toRemove = new HashSet<>();
			int extraPoints = 0;
			
			// Look through our items and see if it would remove any items.
			for (LoadoutItem testItem : items) {
				if (item.wouldRemove(testItem)) {
					// Would remove item so account for it.
					toRemove.add(testItem);
					extraPoints += testItem.getCost();
				}
				else if (testItem.wouldRemove(item)) {
					// Other item would remove this one (but not the other way around) so it can't be added.
					return false;
				}
			}
			
			// If there are still points after adding this item, let it be added
			if (getRemainingPoints() + extraPoints >= item.getCost()) {
				toRemove.forEach(items::remove);
				
				items.add(item);
				if (cat.isSingleItem()) {
					categoryItems.put(cat, item);
				}
				
				updateDisplay();
				return true;
			}
			return false;
		}
	}
	
	public boolean hasItem(LoadoutItem item) {
		return items.contains(item);
	}
	
	public boolean hasUntimelyDemise() {
		return hasItem(LoadoutManager.getManager().getItem(UNTIMELY_DEMISE_NAME));
	}
	
	public boolean hasCategory(Category category) {
		return categoryItems.containsKey(category);
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
	
	public void modifyLoadoutConstruct(LoadoutConstructable construct) {
		boolean hasKit = false;
		for (LoadoutItem item : items) {
			item.modify(this, construct);
			if (item.getCategory() == Category.KIT)
				hasKit = true;
		}
		
		// Apply warrior class if kit is empty
		if (getRemainingPoints() == MAX_POINTS) {
			LoadoutManager.getManager().getDefaultKit().modify(this, construct);
			hasKit = true;
		}
		
		// Add defaults if missing
		if (!hasKit) {
			for (Category category : Category.values()) {
				if (!categoryItems.containsKey(category)) {
					category.giveDefault(construct);
				}
			}
		}
	}





	// EVERYTHING BELOW THIS IS ONLY TEMPORARY UNTIL WE GET DATABASE STUFF
	// ------ SAVING AND LOADING TO FILE ------

	public List<String> toStringList() {
		List<String> strings = new ArrayList<>();
		for (LoadoutItem item : items) {
			strings.add(item.toString());
		}
		return strings;
	}
}
