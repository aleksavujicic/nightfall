package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.effects.GameEffect;
import deimophobe.nightfall.game.entity.ShieldSource;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfPickaxe extends AbstractItem implements CooldownPiece {
	
	DwarfPickaxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc", "pick");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public PickupType getGiveType() { return PickupType.PICK; }
	
	private static final int MAX_CD = 30;
	private static final int MAX_HASTE_CD = 20;
	private int cooldown = 0;
	
	private final ComplexCooldown armourCD = new ComplexCooldown(45*20, null, this::updateShinyness);
	private final ComplexCooldown shinyUpdater = new RepeatingCooldown(15*20, this::updateShinyness);
	
	@Override
	public void update() {
		armourCD.update();
		shinyUpdater.update();
		if (cooldown > 0) {
			cooldown--;
		}
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace face) {
		updateShinyness();
		
		if (click.isRightClick() && cooldown == 0) {
			// PICK REPAIRING ANOTHER DWARF
			Dwarf repairee = dwarf.getLookingAt(5, 2, DwarfManager.getManager().getGamePlayers(), (d) -> d.getArmour().canPickRepair());
			if (repairee != null && armourCD.isAvailable()) {
				GameMap currentMap = GameMap.getCurrentMap();
				if (currentMap.useGold(50)) {
					double frac = (double) currentMap.getCurrentShrineIndex() / currentMap.getNumShrines();
					double maxCD = (30 - 25 * frac)*20;
                    armourCD.setMaxCD((int) maxCD);
					armourCD.reset();
					repairee.getArmour().repair(400);
					GameEffect.DWARF_ARMOUR_CLOUD.playEffect(repairee);
					resetCD();
					return true;
				}
			}
			
			boolean success = tryCraftArmourBlock(clickedBlock, face);
			if (success) {
				resetCD();
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		if (didBreak) {
			switch (block.getType()) {
				case GOLD_BLOCK: {
					if (isHoldingItem()) {
						dwarf.giveConsumable(ConsumableType.ARMOUR_ITEM);
						dwarf.playSound("block.anvil.destroy", 1, 0.5f, true);
					}
					break;
				}
				
				case GOLD_ORE: {
					GameMap.getCurrentMap().mineGold();
					if (Math.random() <= 0.0002 && Game.getGame().getPhase() == Phase.BUILD) {
						GameEffect.LARGE_GOLD_MINE.playEffect(dwarf, block);
					} else {
						GameEffect.SMALL_GOLD_MINE.playEffect(dwarf, block);
					}
					break;
				}
				
				case DIAMOND_ORE: {
					dwarf.addMaxShields(ShieldSource.DIAMOND_ORE);
					GameEffect.DIAMOND_MINE.playEffect(dwarf, block);
					break;
				}
				
				case EMERALD_ORE: {
					dwarf.giveProc(ProcType.EMERALD_ORE);
					GameEffect.EMERALD_MINE.playEffect(dwarf, block);
					break;
				}
				
				case REDSTONE_ORE: {
					dwarf.givePotionEffect(PotionEffectType.FIRE_RESISTANCE, 5*20, 1, true, false, false);
					dwarf.regenMana(3);
					GameEffect.REDSTONE_MINE.playEffect(dwarf, block);
					break;
				}
			}
		}
	}
	
	@Override
	public float getCooldown() {
		return armourCD.getCooldown();
	}
	
	private void resetCD() {
		if (dwarf.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			cooldown = MAX_HASTE_CD;
		} else {
			cooldown = MAX_CD;
		}
	}
	
	// mac cause he wanted his name in the code somewhere
	// and no, no one else gets there name in
	
	private void updateShinyness() {
		setShiny(armourCD.isAvailable());
	}
	
	private static boolean tryCraftArmourBlock(Block clickedBlock, BlockFace blockFace) {
		boolean upgradedArmour
				= NFBlocks.tryConvertBlock(clickedBlock, NFBlocks.CRACKED_GOLD_1, NFBlocks.CRACKED_GOLD_2)
				|| NFBlocks.tryConvertBlock(clickedBlock, NFBlocks.CRACKED_GOLD_2, NFBlocks.CRACKED_GOLD_3)
				|| NFBlocks.tryConvertBlock(clickedBlock, NFBlocks.CRACKED_GOLD_3, NFBlocks.REFINED_GOLD);
		
		if (upgradedArmour) {
			GameEffect.DWARF_ARMOUR_CLOUD.playEffect(clickedBlock);
			return true;
		}
		
		
		Material type = clickedBlock.getType();
		BlockData data = clickedBlock.getBlockData();
		
		if (type != Material.PISTON && type != Material.PISTON_HEAD) return false;
		
		if (data instanceof Piston) {
			Piston piston = (Piston) data;
			if (piston.isExtended() || piston.getFacing() != blockFace) return false;
		} else if (data instanceof PistonHead) {
			PistonHead pistonHead = ((PistonHead) data);
			if (pistonHead.getType() != TechnicalPiston.Type.NORMAL || pistonHead.getFacing() != blockFace) return false;
		} else {
			return false;
		}
		Block armourBlock = clickedBlock.getRelative(blockFace);
		boolean success = NFBlocks.tryConvertBlock(armourBlock, NFBlocks.AIR, NFBlocks.CRACKED_GOLD_1);
		if (success) GameEffect.DWARF_ARMOUR_CLOUD.playEffect(armourBlock);
		return success;
	}
}
