package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HolyAle extends AbstractAle {
	HolyAle(Dwarf dwarf) {
		super(dwarf, 100);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("ale.holy", Slot.MAIN_HAND);
	@Override public ItemStack getItem() { return ITEM; }
	
	@Override
	public void heal() {
		super.heal();
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 3, true, true, true);
	}
}
