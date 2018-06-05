package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.ComparableBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Deimophobe on 28/03/17.
 */
class CraftingConsumable extends Consumable {
	
	private static final int CRAFTING_CD = 2;
	private final Collection<Conversion> conversions;
	
	protected CraftingConsumable(String item, ComparableBlock requiredBlock, ConsumableType newConsumable) {
		this(item, requiredBlock, newConsumable, 1);
	}
	
	protected CraftingConsumable(String item, ComparableBlock requiredBlock, ConsumableType newConsumable, int count) {
		this(item, new Conversion(requiredBlock, newConsumable, count));
	}
	
	protected CraftingConsumable(String item, Conversion... conversions) {
		super(item);
		this.conversions = Arrays.asList(conversions);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) {
			if (clickedBlock == null) return FAILED_CD;
			
			for (Conversion conversion : conversions) {
				boolean success = conversion.tryUseOn(clickedBlock, dwarf);
				if (success) return CRAFTING_CD;
			}
		}
		
		return FAILED_CD;
	}
	
	
	static class Conversion {
		private final ComparableBlock requiredBlock;
		private final ConsumableType newConsumable;
		private final int count;
		
		Conversion(ComparableBlock requiredBlock, ConsumableType newConsumable) {
			this(requiredBlock, newConsumable, 1);
		}
		
		Conversion(ComparableBlock requiredBlock, ConsumableType newConsumable, int count) {
			this.requiredBlock = requiredBlock;
			this.newConsumable = newConsumable;
			this.count = count;
		}
		
		private boolean tryUseOn(Block block, Dwarf dwarf) {
			if (requiredBlock.matchesBlock(block)) {
				dwarf.giveConsumable(newConsumable, count, true);
				
				//TODO fix hack
				if (block.getType() == Material.SPONGE)
					dwarf.playSound("mortar", 0.8f, (float) (1.5 + 0.1*Math.random()), true);
				
				if (block.getType() == Material.IRON_FENCE)
					dwarf.playSound("entity.zombie.attack_door_wood", 0.25f, 2, true);
					
				
				return true;
			} else {
				return false;
			}
		}
	}
}
