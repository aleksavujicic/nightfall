package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.DwarfShovel;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class ProcShovel extends DwarfShovel {

	public ProcShovel(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "procshovel");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
	
	
	@Override
	public void onKill(MonsterDamage damage) {
		if (isMeleeDamageFromItem(damage) && (dwarf.hasProc()))
			dwarf.giveProc(ProcType.REGULAR);
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (block.getType() == Material.GRAVEL && isHoldingItem()) {
			dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 3*20 , 3, true, false, true);
		}
	}
	
	@Override
	protected int getSandGiveAmount() {
		return 2;
	}
	
	@Override
	protected int getCobbleAmount() {
		return 2;
	}
}
