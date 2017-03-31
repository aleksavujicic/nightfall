package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Sound;
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
class Trinket extends AbstractAle {
	Trinket(Dwarf dwarf) {
		super(dwarf, 200);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("ale.trinket", Slot.MAIN_HAND);
	@Override public ItemStack getItem() { return ITEM; }
	
	@Override
	public void heal() {
		dwarf.givePotionEffect(PotionEffectType.ABSORPTION, 720000, 10 ,true, true, true);
		dwarf.givePotionEffect(PotionEffectType.REGENERATION, 720000, 4 ,true, true, true);
		dwarf.playSound("entity.evocation_illager.cast_spell", 1f, 1.5f, false);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
	}
}
