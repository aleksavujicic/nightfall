package deimophobe.nightfall.common.player.cosmetic.hat;

import deimophobe.nightfall.common.items.ItemMatcher;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 11/03/17.
 */
public class Hat implements ItemMatcher {
	private final String identifier;
	private final ItemStack hat;
	
	public Hat(@NotNull String name, @NotNull ItemStack item) {
		this.identifier = name;
		this.hat = item;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void putOn(Player player) {
		player.getInventory().setHelmet(hat);
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack item) {
		return hat.isSimilar(item);
	}
}
