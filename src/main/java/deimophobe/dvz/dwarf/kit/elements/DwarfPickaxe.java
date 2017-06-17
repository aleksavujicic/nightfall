package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.blocks.blocktype.BlockType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.consumable.ConsumableType;
import deimophobe.dvz.effects.GameEffect;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfPickaxe extends AbstractItem {
	
	DwarfPickaxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc.pick");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.PICK; }
	
	private static final int MAX_CD = 30;
	private static final int MAX_HASTE_CD = 15;
	private int cooldown = 0;
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action) && cooldown == 0) {
			if (clickedBlock == null) {
				// PICK REPAIRING ANOTHER DWARF
				Dwarf repairee = dwarf.getLookingAt(2, 5, DwarfManager.getManager().getGamePlayers(), (d) -> !d.getArmour().isAtMax());
				if (repairee != null && ShrineManager.getManager().useGold(50)) {
					repairee.getArmour().repair(600);
					GameEffect.playEffect(GameEffect.DWARF_ARMOUR_CLOUD, repairee);
					
					resetCD();
					return true;
				}
			} else {
				
				boolean success;
				Block affectedBlock;
				if (BlockType.PISTON_BASE.matchesBlock(clickedBlock) && face == BlockFace.UP) {
					// CLICKING ON A PISTON TO CREATE A BLOCK
					
					BlockFace pistonFace = ((Directional) clickedBlock.getState().getData()).getFacing();
					Block goldBlock = clickedBlock.getRelative(pistonFace);
					
					success = BlockType.tryConvertBlock(goldBlock, BlockType.AIR, BlockType.CRACKED_GOLD_1);
					affectedBlock = goldBlock;
				} else {
					success = (
							BlockType.tryConvertBlock(clickedBlock, BlockType.CRACKED_GOLD_1, BlockType.CRACKED_GOLD_2) ||
							BlockType.tryConvertBlock(clickedBlock, BlockType.CRACKED_GOLD_2, BlockType.CRACKED_GOLD_3) ||
							BlockType.tryConvertBlock(clickedBlock, BlockType.CRACKED_GOLD_3, BlockType.REFINED_GOLD));
					affectedBlock = clickedBlock;
				}
				
				if (success) {
					GameEffect.playEffect(GameEffect.DWARF_ARMOUR_CLOUD, affectedBlock);
					
					resetCD();
					return true;
				}
			}
		}
		return false;
	}
	
	private void resetCD() {
		if (dwarf.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING))
			cooldown = MAX_HASTE_CD;
		else
			cooldown = MAX_CD;
	}
	
	@Override
	public void onBlockBreak(Block block) {
		if (block != null && block.getType() == Material.GOLD_BLOCK) {
			dwarf.giveConsumable(ConsumableType.ARMOUR_ITEM);
			dwarf.playSound("block.anvil.destroy", 1, 0.5f, true);
		}
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		if (cooldown > 0)
			cooldown--;
	}
}
