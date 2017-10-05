package deimophobe.nightfall.dwarf.kit.elements;

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
	private final static CustomItem ITEM = DwarvenItems.getBow("crossbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
	@Override public String getBowIdentifier() {return "CROSSBOW";}
	@Override public int getPower() {return POWER;}
	
	private int cooldown = 0;
	private final static int MAX_COOLDOWN = 40;
	
	private final static int ARROW_COST = 2;
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (cooldown > 0)
			cooldown--;
	}
	
	@Override
	public float fractionComplete() {
		return 1 - (float)cooldown/MAX_COOLDOWN;
	}
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (cooldown == 0 && dwarf.hasArrows(ARROW_COST)) {
			fireArrow(3f, 1, 0.05f);
			cooldown = MAX_COOLDOWN;
			
			dwarf.useArrows(ARROW_COST);
			dwarf.playSound("entity.arrow.shoot", 1f, 1.1f, true);
			return true;
		}
		return false;
	}
}
