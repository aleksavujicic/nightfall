package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 26/10/17.
 */
class Minigun extends AbstractBow {
	Minigun(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 60;
	private final static CustomItem ITEM = DwarvenItems.getBow("minigun", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "Minigun";}
	@Override public int getPower() {return POWER;}
	
	private boolean firing = false;
	
	private ComplexCooldown cooldown = new ComplexCooldown(4, this::fireArrow);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		cooldown.update();
		if (firing)
			cooldown.tryUse();
	}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (dwarf.hasFullArrows()) {
			firing = true;
			fireArrow();
			cooldown.reset();
			return true;
		}
		return false;
	}
	
	private void fireArrow() {
		if (dwarf.hasArrows(1) && isHoldingItem()) {
			dwarf.useArrow();
			fireArrow(3f, 1, 1f);
			dwarf.playSound("entity.arrow.shoot", 5f, 0.9f, true);
		} else {
			dwarf.useArrows(10);
			firing = false;
		}
	}
}
