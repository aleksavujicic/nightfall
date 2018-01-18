package deimophobe.nightfall.dwarf.kit.hero.lyra;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.MultipleCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class SilvetrisClaws extends AbstractItem implements KitCooldownPiece {
	
	public SilvetrisClaws(Dwarf dwarf) { super(dwarf); }
	
	private boolean hasLanded = true;
	private final MultipleCooldown leapCD = new MultipleCooldown(60*20, 20*20, this::leap, null);
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "claw");
	@Override public CustomItem getItem() {return ITEM; }
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	@Override public ItemStack getCooldownToggleItem() { return null; }
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		leapCD.update();
		
		if (!hasLanded && dwarf.getPlayer().isOnGround()) hasLanded = true;
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && hasLanded) {
			return leapCD.tryUse();
		}
		return false;
	}
	
	private void leap() {
		hasLanded = false;
		if (dwarf.isSneaking()) {
			dwarf.leap(1, 2);
		} else {
			dwarf.leap(3, 1);
		}
	}
	
	@Override
	public float fractionComplete() {
		return leapCD.fractionComplete();
	}
}
