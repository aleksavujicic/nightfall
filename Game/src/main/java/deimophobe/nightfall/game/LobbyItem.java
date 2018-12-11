package deimophobe.nightfall.game;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 11/12/18.
 */
public interface LobbyItem extends ItemMatcher {
	CustomItem getItem();
	void onClick(Player player);
	
	@Override
	default boolean doesItemMatch(@NotNull ItemStack item) {
		return getItem().doesItemMatch(item);
	}
}
