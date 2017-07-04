package deimophobe.dvz.dwarf.kit;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitCooldownElement extends KitElement {
	float fractionComplete();
	ItemStack getCooldownToggleItem();
}
