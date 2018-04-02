package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.cooldown.RepeatingCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.effects.GameEffect;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Directional;
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
	@Override public KitGiveType getGiveType() { return KitGiveType.PICK; }
	
	private static final int MAX_CD = 30;
	private static final int MAX_HASTE_CD = 15;
	private int cooldown = 0;
	
	private final ComplexCooldown armourCD = new ComplexCooldown(45*20, null, this::updateShinyness);
	private final ComplexCooldown shinyUpdater = new RepeatingCooldown(15*20, this::updateShinyness);
	
	@Override
	public float getCooldown() {
		return armourCD.getCooldown();
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick() && cooldown == 0) {
			updateShinyness();
			
			// PICK REPAIRING ANOTHER DWARF
			Dwarf repairee = dwarf.getLookingAt(5, 2, DwarfManager.getManager().getGamePlayers(), (d) -> d.getArmour().canPickRepair());
			if (repairee != null && armourCD.isAvailable()) {
				GameMap currentMap = GameMap.getCurrentMap();
				if (currentMap.tryUseGold(50)) {
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
			
			boolean success = false;
			Block affectedBlock;
			if (BlockType.PISTON_BASE.matchesBlock(clickedBlock) && face == BlockFace.UP) {
				// CLICKING ON A PISTON TO CREATE A BLOCK
				
				BlockFace pistonFace = ((Directional) clickedBlock.getState().getData()).getFacing();
				Block goldBlock = clickedBlock.getRelative(pistonFace);
				
				success = BlockType.tryConvertBlock(goldBlock, BlockType.AIR, BlockType.CRACKED_GOLD_1);
				affectedBlock = goldBlock;
				
				if (success)
					Sounds.DWARF_MAKE_ARMOUR.playSound(affectedBlock.getLocation());
			} else if (
					BlockType.PISTON_BASE.matchesBlock(clickedBlock.getRelative(0, - 1, 0)) ||
					BlockType.PISTON_BASE.matchesBlock(clickedBlock.getRelative(0, 1, 0)) ||
					BlockType.PISTON_BASE.matchesBlock(clickedBlock.getRelative(-1, 0, 0)) ||
					BlockType.PISTON_BASE.matchesBlock(clickedBlock.getRelative(1, 0, 0)) ||
					BlockType.PISTON_BASE.matchesBlock(clickedBlock.getRelative(0, 0, -1)) ||
					BlockType.PISTON_BASE.matchesBlock(clickedBlock.getRelative(0, 0, 1))){
				success = (
						BlockType.tryConvertBlock(clickedBlock, BlockType.CRACKED_GOLD_1, BlockType.CRACKED_GOLD_2) ||
						BlockType.tryConvertBlock(clickedBlock, BlockType.CRACKED_GOLD_2, BlockType.CRACKED_GOLD_3) ||
						BlockType.tryConvertBlock(clickedBlock, BlockType.CRACKED_GOLD_3, BlockType.REFINED_GOLD));
				affectedBlock = clickedBlock;
			} else {
				affectedBlock = clickedBlock;
			}

			
			if (success) {
				GameEffect.DWARF_ARMOUR_CLOUD.playEffect(affectedBlock);
				
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
					int maxDiamondLevel = (dwarf.hasKitElement(KitPieceType.STRONG_ALE) ? 1 : 5);
					int newLevel = Math.min(dwarf.getPotionEffectLevel(PotionEffectType.ABSORPTION) + 1, maxDiamondLevel);
					int duration = Math.min(dwarf.getPotionEffectDuration(PotionEffectType.ABSORPTION) + 30 * 20, 60 * 20);
					dwarf.givePotionEffect(PotionEffectType.ABSORPTION, duration, newLevel, true, false, true);
					GameEffect.DIAMOND_MINE.playEffect(dwarf, block);
					break;
				}
				
				case EMERALD_ORE: {
					dwarf.giveProc(ProcType.EMERALD_ORE);
					GameEffect.EMERALD_MINE.playEffect(dwarf, block);
					break;
				}
			}
		}
	}
	
	// mac cause he wanted his name in the code somewhere
	// and no, no one else gets there name in
	
	private void updateShinyness() {
		setShiny(armourCD.isAvailable());
	}
	
	@Override
	public void update() {
		armourCD.update();
		shinyUpdater.update();
		if (cooldown > 0)
			cooldown--;
	}
}
