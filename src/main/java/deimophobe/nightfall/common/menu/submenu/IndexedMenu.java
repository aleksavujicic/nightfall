package deimophobe.nightfall.common.menu.submenu;

import deimophobe.nightfall.common.Misc;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 3/05/17.
 */
public abstract class IndexedMenu<T extends SessionData, I> implements SubMenu<T> {
	
	protected final Map<MenuSession<T>, I> storedIndices = new HashMap<>();
	private final Map<I, SubMenu<T>> pages;
	private int size = 0;
	
	public IndexedMenu() {
		this.pages = new HashMap<>();
	}
	
	public void setPage(I index, SubMenu<T> page) {
		pages.put(index, page);
		updateSize();
	}
	
	private void updateSize() {
		int tempSize = 0;
		for (SubMenu<T> page : pages.values())
			tempSize = Math.max(tempSize, page.getSize());
		this.size = tempSize;
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public Map<Integer, ItemStack> getItems(MenuSession<T> session) {
		return getPage(session).getItems(session);
	}
	
	@Override
	public boolean onClick(int i, MenuSession<T> session) {
		return getPage(session).onClick(i, session);
	}
	
	@Override
	public void onClose(MenuSession<T> session) {
		storedIndices.remove(session);
		for (SubMenu<T> page : pages.values())
			page.onClose(session);
	}
	
	protected I getPageIndex(MenuSession<T> session) {
		return storedIndices.computeIfAbsent(session, (k) -> getDefault());
	}
	protected SubMenu<T> getPage(MenuSession<T> session) {
		return pages.get(getPageIndex(session));
	}
	public void setPage(MenuSession<T> session, I index) {
		storedIndices.put(session, index);
	}
	
	protected abstract I getDefault();
	
	
}
