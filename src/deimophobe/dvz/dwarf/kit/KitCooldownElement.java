package deimophobe.dvz.dwarf.kit;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitCooldownElement extends KitElement {
	float fractionComplete();
	ItemStack getCooldownToggleItem();
}
