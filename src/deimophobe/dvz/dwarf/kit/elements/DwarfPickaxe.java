package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.effects.GameEffect;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.Wool;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfPickaxe extends AbstractCooldownItem {
	
	public DwarfPickaxe(Dwarf dwarf) {
		super(dwarf, 20);
	}
	
	private static final ItemStack ITEM = DwarfManager.getManager().getItem("misc.pick");
	@Override public ItemStack getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.PICK; }
	
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action) && isOffCD()) {
			if (clickedBlock == null) {
				// PICK REPAIRING ANOTHER DWARF
				Dwarf repairee = dwarf.getLookingAt(1, 4, (d) -> !d.isMaxArmour(), DwarfManager.getManager());
				if (repairee != null && ShrineManager.getManager().useGold(10)) {
					repairee.repairArmour(200);
					GameEffect.playEffect(GameEffect.DWARF_ARMOUR_CLOUD, repairee);
					
					resetCooldown();
					return true;
				}
			} else {
				Material blockType = clickedBlock.getType();
				
				if (blockType == Material.PISTON_EXTENSION) {
					// CLICKING ON A PISTON TO CREATE A BLOCK
					
					BlockFace pistonFace = ((PistonExtensionMaterial) clickedBlock.getState().getData()).getFacing();
					Block goldBlock = clickedBlock.getRelative(pistonFace);
					if (goldBlock == null || goldBlock.getType() == Material.AIR) {
						// Set to wool
						goldBlock.setType(Material.WOOL);
						
						// Get state and data
						BlockState state = goldBlock.getState();
						Wool wool = (Wool) state.getData();
						
						// Set colour
						wool.setColor(DyeColor.YELLOW);
						
						// Update state and block
						state.setData(wool);
						state.update();
						
						// SOUNDS
						GameEffect.playEffect(GameEffect.DWARF_ARMOUR_CLOUD, state);
						
						resetCooldown();
						return true;
					}
					
				} else if (blockType == Material.WOOL) {
					// CLICKING ON GOLD TO REFINE IT
					BlockState state = clickedBlock.getState();
					Wool wool = (Wool) state.getData();
					
					switch (wool.getColor()) {
						case YELLOW:
							wool.setColor(DyeColor.ORANGE);
							state.setData(wool);
							state.update();
							break;
						case ORANGE:
							wool.setColor(DyeColor.MAGENTA);
							state.setData(wool);
							state.update();
							break;
						case MAGENTA:
							clickedBlock.setType(Material.GOLD_BLOCK);
							break;
						
						default:
							return false;
					}
					
					GameEffect.playEffect(GameEffect.DWARF_ARMOUR_CLOUD, state);
					
					resetCooldown();
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block != null && block.getType() == Material.GOLD_BLOCK) {
			dwarf.giveConsumable(ConsumableType.ARMOUR_ITEM);
			dwarf.playSound("block.anvil.destroy", 1, 0.5f, true);
		}
	}
	
	@Override
	public float fractionComplete() {
		return -1;
	}
}
