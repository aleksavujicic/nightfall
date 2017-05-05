package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 3/05/17.
 */
public class IndexedPageChanger<T extends SessionData, I> extends SimpleItem<T> {
	
	private final IndexedMenu<T, I> menu;
	private final I index;
	
	public IndexedPageChanger(ItemStack item, IndexedMenu<T, I> menu, I index) {
		super(item);
		this.menu = menu;
		this.index = index;
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		menu.setPage(session, index);
		return true;
	}
}