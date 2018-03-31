package deimophobe.nightfall.dwarf.hero;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.DwarvenItems;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class Lyra extends Hero {
	protected Lyra(Player player, HeroType type) {
		super(player, type);
		setMaxArrows(1);
		
		CustomItem clawItem = DwarvenItems.getItem("hero", "claw");
		clawItem.removeAllModifiers();
		setArrowItem(clawItem.createItemStack());
	}
}
