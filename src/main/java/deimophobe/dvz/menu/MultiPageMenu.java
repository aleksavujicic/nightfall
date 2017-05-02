package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 2/05/17.
 */
public class MultiPageMenu<T extends SessionData> implements SubMenu<T> {
	
	private final Map<MenuSession<T>, Integer> pageNumbers = new HashMap<>();
	private final List<SubMenu<T>> pages;
	private int size = 0;
	
	public MultiPageMenu() {
		this.pages = new ArrayList<>();
	}
	
	public MultiPageMenu(List<? extends SubMenu<T>> pages) {
		this.pages = new ArrayList<>(pages);
		updateSize();
	}
	
	public void addPage(SubMenu<T> page) {
		pages.add(page);
		updateSize();
	}
	
	private void updateSize() {
		int tempSize = 0;
		for (SubMenu<T> page : pages)
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
		pageNumbers.remove(session);
		for (SubMenu<T> page : pages)
			page.onClose(session);
	}
	
	protected int getPageNumber(MenuSession<T> session) {
		return pageNumbers.computeIfAbsent(session, p -> 0);
	}
	protected SubMenu<T> getPage(MenuSession<T> session) {
		return pages.get(getPageNumber(session));
	}
	public void changePage(MenuSession<T> session, int increment) {
		setPage(session, getPageNumber(session) + increment);
	}
	public void setPage(MenuSession<T> session, int i) {
		int newI = (i + pages.size()) % pages.size();
		pageNumbers.put(session, newI);
	}
}
