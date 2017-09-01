package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Tombmaker extends AbstractCooldownItem {
	
	Tombmaker(Dwarf dwarf) {
		super(dwarf, 300);
	}
	
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.tombmaker", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
	
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type == DamageType.REGULAR_MELEE && dwarf.hasProc() && isHoldingItem())
			dwarf.giveProc(ProcType.REGULAR);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.playSound("proc", 1, 1, false);
			dwarf.givePotionEffect(PotionEffectType.FAST_DIGGING, 100 , 3, true, false, true);
			resetCooldown();
			return true;
		}
		return false;
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block.getType() == Material.GRAVEL && Game.getGame().getPhase() == Phase.GAME) {
			dwarf.giveProc(ProcType.GRAVEL_PROC);
		}
	}
}
