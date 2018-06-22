package deimophobe.nightfall.game.player;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * This class basically keeps track of all the inventories
 * that need to be iterated through, and iterating through
 * them correctly.
 * Created by Deimophobe on 13/05/18.
 */
class PlayerInventoryIterator implements InventoryIterator {
	
	private final Player player;
	private final boolean reversed;
	
	private final Iterator<InventoryIterator> iterators;
	private InventoryIterator currentIterator;
	
	PlayerInventoryIterator(Player player, boolean reversed) {
		this.player = player;
		this.reversed = reversed;
		
		List<InventoryIterator> iteratorList;
		if (!reversed) {
			iteratorList = Lists.newArrayList(
					new MainInventoryIterator(),
					new CursorIterator(),
					new CraftingWindowIterator()
			);
		} else {
			iteratorList = Lists.newArrayList(
					new CraftingWindowIterator(),
					new MainInventoryIterator(),
					new CursorIterator()
			);
		}
		
		iterators = iteratorList.iterator();
		currentIterator = iterators.next();
	}
	
	@Override
	public void replace(ItemStack newItem) {
		currentIterator.replace(newItem);
	}
	
	@Override
	public boolean hasNext() {
		return currentIterator.hasNext() || iterators.hasNext();
	}
	
	@Override
	public ItemStack next() {
		if (currentIterator.hasNext()) return currentIterator.next();
		currentIterator = iterators.next();
		return next();
	}
	
	
	/**
	 * Represents an iterator that goes through a players main inventory - i.e.
	 * the bottom 4 rows of the inventory screen (including hotbar).
	 */
	private class MainInventoryIterator implements InventoryIterator {
		private final ReversibleInvetoryIterator invetoryIterator;
		private MainInventoryIterator() {
			Inventory inventory = player.getInventory();
			invetoryIterator = new ReversibleInvetoryIterator(inventory, reversed);
		}
		
		@Override
		public void replace(ItemStack newItem) {
			invetoryIterator.replace(newItem);
		}
		
		@Override
		public boolean hasNext() {
			return invetoryIterator.hasNext();
		}
		
		@Override
		public ItemStack next() {
			return invetoryIterator.next();
		}
	}
	
	/**
	 * Represents an iterator that goes through only the item on
	 * the players cursor.
	 */
	private class CursorIterator implements InventoryIterator {
		private boolean iterated = false;
		
		@Override
		public void replace(ItemStack newItem) {
			if (!iterated) throw new NoSuchElementException();
			player.setItemOnCursor(newItem);
		}
		
		@Override
		public boolean hasNext() {
			return !iterated;
		}
		
		@Override
		public ItemStack next() {
			if (iterated) throw new NoSuchElementException();
			iterated = true;
			return player.getItemOnCursor();
		}
	}
	
	
	/**
	 * Represents an iterator that goes through the 2x2 crafting
	 * window (if it is open).
	 */
	private class CraftingWindowIterator implements InventoryIterator {
		private final ReversibleInvetoryIterator invetoryIterator;
		private final boolean opened;
		
		private CraftingWindowIterator() {
			InventoryView view = player.getOpenInventory();
			invetoryIterator = new ReversibleInvetoryIterator(view.getTopInventory(), reversed);
			opened = (view.getType() == InventoryType.CRAFTING);
		}
		
		@Override
		public void replace(ItemStack newItem) {
			checkValid();
			invetoryIterator.replace(newItem);
		}
		
		@Override
		public boolean hasNext() {
			return opened && invetoryIterator.hasNext();
		}
		
		@Override
		public ItemStack next() {
			checkValid();
			return invetoryIterator.next();
		}
		
		private void checkValid() {
			if (!opened) throw new NoSuchElementException();
		}
	}
	
	/**
	 * A helper class to easily enable reversing iteration through
	 * an inventory.
	 */
	private static class ReversibleInvetoryIterator implements InventoryIterator {
		private final ListIterator<ItemStack> listerator;
		private final boolean reversed;
		
		private ReversibleInvetoryIterator(Inventory inventory, boolean reversed) {
			this.reversed = reversed;
			if (!reversed) {
				listerator = inventory.iterator();
			} else {
				listerator = inventory.iterator(inventory.getSize());
			}
		}
		
		@Override
		public void replace(ItemStack newItem) {
			listerator.set(newItem);
		}
		
		@Override
		public boolean hasNext() {
			if (!reversed) {
				return listerator.hasNext();
			} else {
				return listerator.hasPrevious();
			}
		}
		
		@Override
		public ItemStack next() {
			if (!reversed) {
				return listerator.next();
			} else {
				return listerator.previous();
			}
		}
	}
}
