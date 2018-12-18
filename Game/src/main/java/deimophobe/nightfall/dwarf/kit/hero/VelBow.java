package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.dwarf.kit.ranged.AbstractPowerBow;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class VelBow extends AbstractPowerBow {
	
	public VelBow(Dwarf dwarf) {
		super(dwarf, 25*20, ChatColor.DARK_PURPLE, 400, 0.2, ProcType.VELBOW);
	}
	
	private final static int POWER = 70;
	private final static CustomItem ITEM = getBow("hero", "velbow", POWER);
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getGiveType() { return PickupType.START; }
	@Override public String getBowIdentifier() {return "VELBOW";}
	@Override public int getPower() {return POWER;}
	
	@Override
	protected void onPowerFire(Arrow poweredArrow) {
		super.onPowerFire(poweredArrow);
		poweredArrow.setKnockbackStrength(10);
	}
}
