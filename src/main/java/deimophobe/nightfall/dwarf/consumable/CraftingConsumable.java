package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Deimophobe on 28/03/17.
 */
class CraftingConsumable extends Consumable {
	
	private static final int CRAFTING_CD = 2;
	private final Collection<Conversion> conversions;
	
	protected CraftingConsumable(String item, Material clickMaterial, ConsumableType newConsumable) {
		this(item, clickMaterial, newConsumable, 1);
	}
	
	protected CraftingConsumable(String item, Material clickMaterial, ConsumableType newConsumable, int count) {
		this(item, new Conversion(clickMaterial, newConsumable, count));
	}
	
	protected CraftingConsumable(String item, Conversion... conversions) {
		super(item);
		this.conversions = Arrays.asList(conversions);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) {
			if (clickedBlock == null) return FAILED_CD;
			
			Material blockMat = clickedBlock.getType();
			
			for (Conversion conversion : conversions) {
				boolean success = conversion.tryUseOn(blockMat, dwarf);
				if (success) return CRAFTING_CD;
			}
		}
		
		return FAILED_CD;
	}
	
	
	static class Conversion {
		private final Material required;
		private final ConsumableType newConsumable;
		private final int count;
		
		Conversion(Material required, ConsumableType newConsumable) {
			this(required, newConsumable, 1);
		}
		
		Conversion(Material required, ConsumableType newConsumable, int count) {
			this.required = required;
			this.newConsumable = newConsumable;
			this.count = count;
		}
		
		private boolean tryUseOn(Material material, Dwarf dwarf) {
			if (material == required) {
				dwarf.giveConsumable(newConsumable, count);
				
				//TODO fix hack
				if (material == Material.SPONGE)
					dwarf.playSound("mortar", 0.8f, (float) (1.5 + 0.1*Math.random()), true);
				
				if (material == Material.IRON_FENCE)
					dwarf.playSound("entity.zombie.attack_door_wood", 0.25f, 2, true);
					
				
				return true;
			} else {
				return false;
			}
		}
	}
}
