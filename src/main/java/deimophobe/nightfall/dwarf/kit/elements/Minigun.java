package deimophobe.nightfall.dwarf.kit.elements;

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
 * Created by Deimophobe on 26/10/17.
 */
class Minigun extends AbstractBow implements KitCooldownElement {
	Minigun(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static int POWER = 60;
	private final static CustomItem ITEM = DwarvenItems.getBow("minigun", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
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
	public float fractionComplete() {
		return cooldown.fractionComplete();
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
		if (dwarf.hasArrows(1)) {
			dwarf.useArrows(1);
			fireArrow(3f, 1, 0.05f);
			dwarf.playSound("entity.arrow.shoot", 1f, 0.9f, true);
		} else {
			firing = false;
		}
	}
}
