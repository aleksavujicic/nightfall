package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Rat extends AbstractMob {
	
	Rat(MonsterPlayer monster) {
		super(monster, MobType.RAT);
	}
	
	private int stealCD = 0;
	private static final int STEAL_MAX_CD = 5;
	
	private boolean preparingJump = false;
	private int jumpCD = 0;
	private static final int JUMP_TIME = 20;
	private static final int JUMP_ALLOWANCE = 13;
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		monster.givePermanentPotionEffect(PotionEffectType.JUMP, -2);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (sneaking) prepareJump();
		else tryJump();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (stealCD > 0)
			stealCD--;
		
		if (preparingJump) {
			if (jumpCD < JUMP_TIME)
				jumpCD++;
		}
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (stealCD == 0 && Misc.isRightClick(action) && clickedBlock != null) {
			if (BlockType.ACTIVE_SHRINE_BLOCK.matchesBlock(clickedBlock) && GameMap.getCurrentMap().hasGold()) {
				playSound("steal");
				monster.gainXP(2, false);
				GameMap.getCurrentMap().stealGold(1);
				stealCD = STEAL_MAX_CD;
			}
		}
	}
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		super.onBlockBreak(block, didBreak);
		if (block.getType() == Material.TORCH && didBreak)
			playSound("torch");
	}
	
	@Override
	public float getCooldown() {
		return (float) jumpCD/JUMP_TIME;
	}
	
	private void prepareJump() {
		preparingJump = true;
		jumpCD = 0;
		monster.givePermanentPotionEffect(PotionEffectType.SLOW, 3);
	}
	
	private void tryJump() {
		preparingJump = false;
		monster.removePotionEffect(PotionEffectType.SLOW);
		if (monster.getPlayer().isOnGround() && jumpCD >= JUMP_TIME - JUMP_ALLOWANCE) {
			double weaker = 1 - (double)(JUMP_TIME - jumpCD)/JUMP_ALLOWANCE;
			monster.leap(weaker * 1, weaker * 0.55);
		}
		jumpCD = 0;
	}
}
