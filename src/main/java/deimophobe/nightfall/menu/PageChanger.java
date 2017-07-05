package deimophobe.nightfall.menu;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/05/17.
 */
public class PageChanger<T extends SessionData> extends SimpleItem<T> {
	
	private final MultiPageMenu<T> menu;
	private final boolean forward;
	
	public PageChanger(ItemStack item, MultiPageMenu<T> menu, boolean forward) {
		super(item);
		this.menu = menu;
		this.forward = forward;
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		if (forward) menu.changePage(session, 1);
		else menu.changePage(session, -1);
		return true;
	}
}
