package deimophobe.nightfall.game;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 11/12/18.
 */
public interface LobbyItem extends ItemMatcher {
	/** The item for the given player. If player is null, then should return default item. */
	CustomItem getItem(@Nullable Player player);
	void onClick(Player player);
	
	@Override
	default boolean doesItemMatch(@NotNull ItemStack item) {
		return getItem(null).doesItemMatch(item);
	}
}
