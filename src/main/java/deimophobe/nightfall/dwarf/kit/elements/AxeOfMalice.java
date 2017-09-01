package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
class AxeOfMalice extends AbstractCooldownItem {
	
	AxeOfMalice(Dwarf dwarf) {
		super(dwarf, 1200);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.axe", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	@Override
	public void onKill(GameEntity monster, DamageType b) {
		reduceCooldown(20);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.giveProc(ProcType.MALICE);
			dwarf.playSound("maliceuse", 20f, 1f, false);
			resetCooldown();
			return true;
		}
		return false;
	}
	
	@Override
	public void onOffCD() {
		dwarf.playSound("entity.elder_guardian.curse", 1, 1f, false);
	}
}
