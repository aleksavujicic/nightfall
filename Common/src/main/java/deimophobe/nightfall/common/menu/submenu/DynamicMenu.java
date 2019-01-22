package deimophobe.nightfall.common.menu.submenu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Deimophobe on 15/01/19.
 */
public abstract class DynamicMenu<T extends SessionData> implements SubMenu<T> {
	private final int size;
	private final Map<MenuSession<T>, SubMenu<T>> menuCache;
	
	public DynamicMenu(int size) {
		this(size, true);
	}
	
	public DynamicMenu(int size, boolean cache) {
		this.size = size;
		
		if (cache) {
			menuCache = new HashMap<>();
		} else {
			menuCache = null;
		}
	}
	
	@Override
	public final int getSize() {
		return size;
	}
	
	@Override
	public Map<Integer, ItemStack> getItems(MenuSession<T> session) {
		return tryGetCachedMenu(session).getItems(session);
	}
	
	@Override
	public boolean onClick(int i, MenuSession<T> session) {
		return tryGetCachedMenu(session).onClick(i, session);
	}
	
	@Override
	public void onClose(MenuSession<T> session) {
		tryGetCachedMenu(session).onClose(session);
		if (menuCache != null) menuCache.remove(session);
	}
	
	@NotNull
	protected abstract SubMenu<T> getMenu(MenuSession<T> session);
	
	private SubMenu<T> tryGetCachedMenu(MenuSession<T> session) {
		if (menuCache == null) return getMenu(session);
		
		SubMenu<T> cached = menuCache.get(session);
		if (cached != null) return cached;
		
		SubMenu<T> subMenu = getMenu(session);
		menuCache.put(session, subMenu);
		return subMenu;
	}
	
	public final void resetMenu(MenuSession<T> session) {
		checkState(menuCache != null, "Cannot reset menu when DynamicMenu has no cache.");
		
		SubMenu<T> subMenu = getMenu(session);
		menuCache.put(session, subMenu);
	}
	
	public final void setMenu(MenuSession<T> session, SubMenu<T> subMenu) {
		checkState(menuCache != null, "Cannot set menu when DynamicMenu has no cache.");
		
		menuCache.put(session, subMenu);
	}
}
