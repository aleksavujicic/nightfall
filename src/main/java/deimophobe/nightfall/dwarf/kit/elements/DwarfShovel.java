package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
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
			if (Game.getGame().getPhase() == Phase.BUILD)
				dwarf.giveConsumable(ConsumableType.COBBLESTONE, 4, true);
			else
				dwarf.giveConsumable(ConsumableType.COBBLESTONE, 2, true);
			dwarf.playSound("block.anvil.place", 0.2f, 0.8f, true);
			dwarf.playSound("block.anvil.break", 1f, 0.8f, true);
		}
	}
}
