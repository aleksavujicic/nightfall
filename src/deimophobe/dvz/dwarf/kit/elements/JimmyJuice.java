package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class JimmyJuice extends AbstractAle {
	JimmyJuice(Dwarf dwarf) {
		super(dwarf, 100);
	}
	
	private final static ItemStack ITEM = DwarvenItems.getItem("ale.jj", Slot.MAIN_HAND);
	@Override public ItemStack getItem() { return ITEM; }
	
	@Override
	public void onLateGotHit(GameEntity monster, DamageType type, double damage) {
		double health = dwarf.getPlayer().getHealth();
		if (health - damage <= 0.1 || health <= 16) {
			if (dwarf.tryUseMana(120)) {
				heal();
			}
		}
	}
}
