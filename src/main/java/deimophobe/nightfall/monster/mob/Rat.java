package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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
	
	private boolean jumpState;
	private ComplexCooldown toggleCD = new ComplexCooldown(4*20, this::toggleJumpState);
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		jumpState = true;
		toggleJumpState();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (!sneaking) toggleCD.tryUse();
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (stealCD > 0)
			stealCD--;
		
		toggleCD.update();
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
		return toggleCD.fractionComplete();
	}
	
	private void toggleJumpState() {
		jumpState = !jumpState;
		Player player = monster.getPlayer();
		if (jumpState) {
			monster.givePermanentPotionEffect(PotionEffectType.SLOW, 4);
			monster.removePotionEffect(PotionEffectType.JUMP);
			player.setFoodLevel(0);
		} else {
			monster.givePermanentPotionEffect(PotionEffectType.JUMP, -5);
			monster.removePotionEffect(PotionEffectType.SLOW);
			player.setFoodLevel(20);
		}
	}
}
