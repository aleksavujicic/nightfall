package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.*;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Tombmaker extends AbstractCooldownItem {
	
	Tombmaker(Dwarf dwarf) {
		super(dwarf, 300);
	}
	
	
	private final static ItemStack ITEM = DwarvenItems.getItem("sword.tombmaker", Slot.MAIN_HAND);
	@Override public ItemStack getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
	
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type == DamageType.REGULAR_MELEE && dwarf.hasProc() && isHoldingItem())
			dwarf.giveProc(Dwarf.ProcType.REGULAR);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.playSound("proc", 1, 1, false);
			dwarf.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 2), true);
			resetCooldown();
			return true;
		}
		return false;
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block.getType() == Material.GRAVEL && Game.getGame().getPhase() == Phase.GAME) {
			dwarf.giveProc(Dwarf.ProcType.GRAVEL_PROC);
		}
	}
}
