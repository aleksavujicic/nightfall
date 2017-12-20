package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/03/17.
 */
public class DwarfShovel extends AbstractItem {
	protected DwarfShovel(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc", "shovel");
	@Override public CustomItem getItem() {return ITEM;}
	
	
	@Override
	public KitGiveType getGiveType() {
		return KitGiveType.SHOVEL;
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (didBreak && block.getType() == Material.GRAVEL) {
			int quantity = 2;
			
			//if (Game.getGame().getPhase() == Phase.BUILD) quantity = 4;
			//else quantity = 2;
			
			dwarf.giveConsumable(ConsumableType.COBBLESTONE, quantity, true);
			
			dwarf.playSound("block.anvil.place", 0.2f, 0.8f, true);
			dwarf.playSound("block.anvil.break", 1f, 0.8f, true);
		}
	}
}
