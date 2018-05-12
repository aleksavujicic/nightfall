package deimophobe.nightfall.game;

import org.bukkit.inventory.ItemStack;

import java.util.ListIterator;

/**
 * Created by Deimophobe on 13/05/18.
 */
public class PlayerInventoryListIterator implements ListIterator<ItemStack> {
	
	private int iteratorPosition;
	
	@Override
	public boolean hasNext() {
		return false;
	}
	
	@Override
	public ItemStack next() {
		return null;
	}
	
	@Override
	public boolean hasPrevious() {
		return false;
	}
	
	@Override
	public ItemStack previous() {
		return null;
	}
	
	@Override
	public int nextIndex() {
		return 0;
	}
	
	@Override
	public int previousIndex() {
		return 0;
	}
	
	@Override
	public void remove() {
	
	}
	
	@Override
	public void set(ItemStack itemStack) {
	
	}
	
	@Override
	public void add(ItemStack itemStack) {
	
	}
}
