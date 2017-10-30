package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Crossbow extends AbstractBow implements KitCooldownElement {
	Crossbow(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 100;
	private final static int FIRING_POWER = 60;
	private final static CustomItem ITEM = DwarvenItems.getBow("crossbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
	@Override public String getBowIdentifier() {return "CROSSBOW";}
	
	private ComplexCooldown arrowCD = new ComplexCooldown(40, this::fireNormalArrow);
	
	private boolean firing = false;
	private ComplexCooldown rapidCD = new ComplexCooldown(4, this::fireRapidArrow);
	private ComplexCooldown longRapid = new ComplexCooldown(20*20, this::startFiring);
	
	private final static int ARROW_COST = 2;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		arrowCD.update();
		rapidCD.update();
		longRapid.update();
		
		if (firing)
			rapidCD.tryUse();
	}
	
	@Override
	public float fractionComplete() {
		return longRapid.fractionComplete();
	}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (firing) {
			stopFiring();
			return true;
		}
		
		if (Misc.isRightClick(action) && dwarf.hasArrows(ARROW_COST)) {
			arrowCD.tryUse();
			return true;
		} else if (dwarf.hasArrows(1)) {
			longRapid.tryUse();
			return true;
		}
		return false;
	}
	
	@Override
	public int getPower() {
		if (firing)
			return FIRING_POWER;
		else
			return POWER;
	}
	
	private void fireNormalArrow() {
		fireArrow(3f, 1, 0.05f);
		dwarf.useArrows(ARROW_COST);
		dwarf.playSound("entity.arrow.shoot", 1f, 1.1f, true);
		dwarf.playSound("entity.shulker.shoot", 1f, 0.8f, true);
	}
	
	private void startFiring() {
		firing = true;
		fireRapidArrow();
		rapidCD.reset();
	}
	
	private void stopFiring() {
		firing = false;
	}
	
	private void fireRapidArrow() {
		if (dwarf.hasArrows(1) && isHoldingItem()) {
			dwarf.useArrow();
			fireArrow(3f, 1, 10f);
			dwarf.playSound("entity.arrow.shoot", 5f, 0.9f, true);
		} else {
			stopFiring();
		}
	}
}
