package deimophobe.nightfall.dwarf.kit.hero.lyra;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.MultipleCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class SilvetrisClaws extends AbstractItem implements CooldownPiece {
	
	public SilvetrisClaws(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private boolean hasLanded = true;
	private final MultipleCooldown leapCD = new MultipleCooldown(60*20, 20*20, this::leap, null);
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "claw");
	@Override public CustomItem getItem() {return ITEM; }
	@Override public PickupType getPickupType() {return PickupType.START;}
	
	@Override
	public void update() {
		super.update();
		leapCD.update();
		
		if (!hasLanded && dwarf.getPlayer().isOnGround()) hasLanded = true;
	}
	
	@Override
	public boolean onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick() && hasLanded) {
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
	public float getCooldown() {
		return leapCD.getCooldown();
	}
}
