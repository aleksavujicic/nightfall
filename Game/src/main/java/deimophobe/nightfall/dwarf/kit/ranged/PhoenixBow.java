package deimophobe.nightfall.dwarf.kit.ranged;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.ChatColor;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class PhoenixBow extends AbstractPowerBow {
	
	public PhoenixBow(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type, 30*20, ChatColor.RED, 200, 0.5, ProcType.DRAGONSKIN);
	}
	
	private final static int POWER = 50;
	private final static CustomItem ITEM = getBow("dragonskin", POWER);
	@Override public CustomItem getItem() { return ITEM; }
	@Override public String getBowIdentifier() {return "PHOENIX_BOW";}
	@Override public int getPower() {return POWER;}
}
