package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 2/05/17.
 */
public class CompositeMenu<T extends SessionData> implements SubMenu<T> {
	
	private final List<SubMenu<T>> subMenus;
	private int size = 0;
	
	public CompositeMenu() {
		this.subMenus = new ArrayList<>();
	}
	
	public CompositeMenu(List<? extends SubMenu<T>> subMenus) {
		this.subMenus = new ArrayList<>(subMenus);
		updateSize();
	}
	
	public void addSubMenu(SubMenu<T> subMenu) {
		subMenus.add(subMenu);
		updateSize();
	}
	
	private void updateSize() {
		int tempSize = 0;
		for (SubMenu<T> subMenu : subMenus)
			tempSize += subMenu.getSize();
		this.size = tempSize;
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public Map<Integer, ItemStack> getItems(MenuSession<T> session) {
		Map<Integer, ItemStack> items = new HashMap<>();
		int offset = 0;
		for (SubMenu<T> subMenu : subMenus) {
			for (Integer key : subMenu.getItems(session).keySet()) {
				items.put(key + offset, subMenu.getItems(session).get(key));
			}
			offset += subMenu.getSize();
		}
		return items;
	}
	
	@Override
	public boolean onClick(int i, MenuSession<T> session) {
		for (SubMenu<T> subMenu : subMenus) {
			int menuSize = subMenu.getSize();
			if (i < menuSize)
				return subMenu.onClick(i, session);
			else
				i -= menuSize;
		}
		return false;
	}
	
	@Override
	public void onClose(MenuSession<T> session) {
		for (SubMenu<T> subMenu : subMenus)
			subMenu.onClose(session);
	}
}
