package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.NFBlocks;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Rat extends AbstractMob {
	
	Rat(MonsterPlayer monster) {
		super(monster, MobType.RAT);
	}
	
	private int stealCD = 0;
	private static final int STEAL_MAX_CD = 10;
	
	private boolean jumpState;
	private ComplexCooldown toggleCD = new ComplexCooldown(1, this::toggleJumpState);
	
	@Override
	public void onSpawn(SpawnMethod spawnMethod) {
		super.onSpawn(spawnMethod);
		jumpState = true;
		toggleJumpState();
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		if (sneaking) toggleCD.tryUse();
	}
	
	@Override
	public void update() {
		super.update();
		if (stealCD > 0)
			stealCD--;
		
		toggleCD.update();
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		damage.multiplyKnockback(0.75);
	}
	
	@Override
	public void onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (stealCD == 0 && click.isRightClick() && clickedBlock != null) {
			GameMap map = GameMap.getCurrentMap();
			if (!NFBlocks.ACTIVE_SHRINE_BLOCK.matchesBlock(clickedBlock)) return;
			if (!map.getCurrentShrineRegion().containsPlayer(monster)) return;
			
			if (map.hasGold()) {
				monster.giveExperience(5);
				map.stealGold(3);
			} else {
				monster.giveExperience(2);
			}
			
			playSound("steal");
			stealCD = STEAL_MAX_CD;
		}
	}
	
	@Override
	public boolean onBlockBreak(Block block, boolean didBreak) {
		didBreak = super.onBlockBreak(block, didBreak);
		if (block.getType() == Material.TORCH && didBreak)
			playSound("torch");
		
		return didBreak;
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
