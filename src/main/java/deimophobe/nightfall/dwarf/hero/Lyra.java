package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.dwarf.DwarvenItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class Lyra extends Hero {
	protected Lyra(Player player, HeroType type) {
		super(player, type);
		setMaxArrows(1);
	}
	
	private static final ItemStack CLAW = DwarvenItems.getItem("hero", "claw").createItemStack();
	@Override
	protected ItemStack getArrow() {
		return CLAW;
	}
}
