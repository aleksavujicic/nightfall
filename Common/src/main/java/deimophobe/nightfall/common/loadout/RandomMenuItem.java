package deimophobe.nightfall.common.loadout;

import com.google.common.collect.Sets;
import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class RandomMenuItem extends SimpleItem<Loadout> {
	
	public RandomMenuItem(ItemStack itemStack) {
		super(itemStack);
	}
	
	@Override
	public boolean onClick(MenuSession<Loadout> session) {
		Loadout loadout = session.getData();
		int pointsRemaining = loadout.getRemainingPoints();
		if (pointsRemaining == 0) return false;
		
		
		Set<LoadoutItem> randomPool = new HashSet<>();
		Set<LoadoutItem> chosenItems = new HashSet<>();
		
		// Go through all categories
		for (Category category : Category.values()) {
			if (category == Category.KIT) continue;
			if (loadout.hasCategory(category)) continue;
			
			if (category.isSingleItem()) {
				Collection<LoadoutItem> items = getRandomItems(category.getItems(), pointsRemaining, false);
				if (items != null) {
					chosenItems.addAll(items);
					for (LoadoutItem item : items) pointsRemaining -= item.getCost();
				}
			} else {
				randomPool.addAll(category.getItems());
			}
		}
		
		randomPool.removeIf(loadout::hasItem);
		
		// NOTE: There is a bug where if no getRandomItems cannot find a maximal solution (i.e. choosing remaining items
		// so that there are no points left), then it will return null, instead of finding an optimal (next best) solution.
		
		// This means a player will get no extra items if the remaining items cannot be used to decrease points to 0.
		// Fortunately this is a very rare situation in the current setup we have.
		
		// Randomly choose from remaing items
		Collection<LoadoutItem> items = getRandomItems(randomPool, pointsRemaining, true);
		if (items != null) chosenItems.addAll(items);
		
		chosenItems.forEach(loadout::selectItem);
		
		playSound(session.getPlayer());
		return true;
	}
	
	private void playSound(Player player) {
		player.playSound(player.getLocation(), "entity.villager.yes", 1f, 1f);
	}
	
	private static Set<LoadoutItem> getRandomItems(Set<LoadoutItem> items, int pointLimit, boolean recurse) {
		// Shuffle everything
		List<LoadoutItem> itemList = new ArrayList<>(items);
		Collections.shuffle(itemList);
		
		for (LoadoutItem item : itemList) {
			int cost = item.getCost();
			if (cost > pointLimit) continue; // Too expensive to add
			
			if (cost == pointLimit || !recurse) {
				// Price is just right - can return new item set and add items
				// by going up the stack trace
				return Sets.newHashSet(item);
			}
			
			// Can add item but also have points remaining.
			
			Set<LoadoutItem> newItems = new HashSet<>(items);
			newItems.remove(item);
			// Repeat process but dont look at this item (and subtract its cost)
			Set<LoadoutItem> finishedItems = getRandomItems(newItems, pointLimit - cost, recurse);
			
			// If failed to build a list, try next item
			if (finishedItems == null) continue;
			
			// Otherwise success, and keep returning
			finishedItems.add(item);
			return finishedItems;
		}
		
		// No items were successfully added. Notify failure to caller
		return null;
	}
}
