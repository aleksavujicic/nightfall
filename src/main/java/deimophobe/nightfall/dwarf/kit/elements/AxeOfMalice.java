package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class AxeOfMalice extends AbstractItem implements KitCooldownElement {
	
	private final ComplexCooldown cd = new ComplexCooldown(60, this::giveProc, this::notifyOffCD);
	
	AxeOfMalice(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.axe", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() { return ITEM.createItemStack();}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		cd.reduceCooldown(20);
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			cd.tryUse();
		}
		return false;
	}
	
	private void giveProc() {
		dwarf.giveProc(ProcType.MALICE);
		dwarf.playSound("maliceuse", 20f, 1f, false);
	}
	
	private void notifyOffCD() {
		dwarf.playSound("entity.elder_guardian.curse", 1, 1f, false);
	}
	
	@Override
	public float fractionComplete() {
		return cd.fractionComplete();
	}
	
}
