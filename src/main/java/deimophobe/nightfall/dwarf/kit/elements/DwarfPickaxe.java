package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitCooldownElement;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.effects.GameEffect;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfPickaxe extends AbstractItem implements KitCooldownElement {
	
	DwarfPickaxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc.pick");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.PICK; }
	@Override public ItemStack getCooldownToggleItem() {return ITEM.createItemStack();}
	
	private static final int MAX_CD = 30;
	private static final int MAX_HASTE_CD = 15;
	private int cooldown = 0;
	
	private final ComplexCooldown armourCD = new ComplexCooldown(45*20);
	
	@Override
	public float fractionComplete() {
		return armourCD.fractionComplete();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action) && cooldown == 0) {
			// PICK REPAIRING ANOTHER DWARF
			Dwarf repairee = dwarf.getLookingAt(2, 5, DwarfManager.getManager().getGamePlayers(), (d) -> !d.getArmour().isAtMax());
			if (repairee != null && armourCD.isAvailable()) {
				GameMap currentMap = GameMap.getCurrentMap();
				if (currentMap.tryUseGold(50)) {
                    armourCD.setMaxCD(30 * 20 - 25 * 20 * currentMap.getCurrentShrineIndex() / (currentMap.getNumShrines()-1));
					armourCD.reset();
					repairee.getArmour().repair(400);
					GameEffect.playEffect(GameEffect.DWARF_ARMOUR_CLOUD, repairee);
					resetCD();
					return true;
				}
			}
			
			boolean success;
			Block affectedBlock;
			if (BlockType.PISTON_BASE.matchesBlock(clickedBlock) && face == BlockFace.UP) {
				// CLICKING ON A PISTON TO CREATE A BLOCK
				
				BlockFace pistonFace = ((Directional) clickedBlock.getState().getData()).getFacing();
				Block goldBlock = clickedBlock.getRelative(pistonFace);
				
				success = BlockType.tryConvertBlock(goldBlock, BlockType.AIR, BlockType.CRACKED_GOLD_1);
				affectedBlock = goldBlock;
				
				if (success)
					Sounds.DWARF_MAKE_ARMOUR.playSound(affectedBlock.getLocation());
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
		return false;
	}
	
	private void resetCD() {
		if (dwarf.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING))
			cooldown = MAX_HASTE_CD;
		else
			cooldown = MAX_CD;
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		if (block != null && block.getType() == Material.GOLD_BLOCK && didBreak) {
			dwarf.giveConsumable(ConsumableType.ARMOUR_ITEM);
			dwarf.playSound("block.anvil.destroy", 1, 0.5f, true);
		}
	}
	
	@Override
	public void update(boolean a, boolean b, boolean c, boolean d, boolean e) {
		armourCD.update();
		if (cooldown > 0)
			cooldown--;
	}
}
