package deimophobe.nightfall.dwarf.kit.elements.ranged;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Dragonskin extends AbstractPowerBow {
	
	public Dragonskin(Dwarf dwarf) {
		super(dwarf, 30*20, ChatColor.RED, 200, 0.5, ProcType.DRAGONSKIN);
	}
	
	private final static int POWER = 50;
	private final static CustomItem ITEM = getBow("dragonskin", POWER);
	@Override public CustomItem getItem() { return ITEM; }
	@Override public String getBowIdentifier() {return "DRAGONSKIN";}
	@Override public int getPower() {return POWER;}
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
}
