package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.LoadoutData;
import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import deimophobe.nightfall.common.menu.SessionData;

import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
public class Loadout implements SessionData, Datable<LoadoutData> {
	
	private static final String UNTIMELY_DEMISE_NAME = "untimely";
	
	public static final int MAX_POINTS = 64;
	
	private final SortedSet<LoadoutItem> items = new TreeSet<>();
	
	
	public Loadout() { }
	
	public Loadout(LoadoutData data) {
		for (String item : data.items) {
			LoadoutItem loadoutItem = LoadoutManager.getManager().getItem(item);
			selectItem(loadoutItem);
		}
	}
	
	@Override
	public LoadoutData toData() {
		LoadoutData data = new LoadoutData();
		List<String> itemList = new ArrayList<>();
		for (LoadoutItem item : items) {
			itemList.add(item.toString());
		}
		data.items = itemList;
		return data;
	}
	
	public boolean selectItem(LoadoutItem item) {
		if (items.contains(item)) {
			items.remove(item);
			
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
		for (LoadoutItem item : items) {
			if (item.getCategory() == category) return true;
		}
		return false;
	}
	
	public int getRemainingPoints() {
		int usedPoints = 0;
		for (LoadoutItem item : items) {
			usedPoints += item.getCost();
		}
		return MAX_POINTS - usedPoints;
	}
	
	public void clear() {
		items.removeIf(item -> item.getCost() != 0);
		
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
			if (item.getCategory() == Category.KIT) {
				hasKit = true;
			}
		}
		
		// Apply warrior class if kit is empty
		if (getRemainingPoints() == MAX_POINTS) {
			LoadoutManager.getManager().getDefaultKit().modify(this, construct);
			hasKit = true;
		}
		
		// Add defaults if missing
		if (!hasKit) {
			for (Category category : Category.values()) {
				if (!hasCategory(category)) {
					category.giveDefault(construct);
				}
			}
		}
	}
}
