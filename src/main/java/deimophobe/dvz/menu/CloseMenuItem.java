package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

import java.awt.*;

/**
 * Created by Deimophobe on 16/06/17.
 */
public class CloseMenuItem<T extends SessionData> extends SimpleItem<T> {
	public CloseMenuItem(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		session.closeSession();
		return false;
	}
}
