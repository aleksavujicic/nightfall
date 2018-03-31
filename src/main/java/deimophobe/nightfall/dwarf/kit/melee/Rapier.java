package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.CooldownPiece;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.monster.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 28/10/17.
 */
public class Rapier extends AbstractItem implements CooldownPiece {
	
	private int stacks = 0;
	private final static int JUMP_COST = 8;
	private final static int MAX_JUMPS = 8;
	private final static int MAX_STACKS = MAX_JUMPS * JUMP_COST;
	
	private final Set<MonsterEntity<?>> damagedEntities = new HashSet<>();
	private final ComplexCooldown leapCD = new ComplexCooldown(10, this::leap);
	
	public Rapier(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "rapier");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
	
	
	
	@Override
	public void update() {
		super.update();
		leapCD.update();
		
		if (!leapCD.isAvailable()) {
			dwarf.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, dwarf.getLocation(), 1, 0, 0, 0, 0);
			dwarf.getWorld().spawnParticle(Particle.FLAME, dwarf.getLocation(), 2, 0.05, 0.05, 0.05, 0.05);
			for (MonsterEntity<?> monster : MonsterManager.getManager().getAliveMobsAndAIs()) {
				if (damagedEntities.contains(monster)) continue;
				
				if (monster.getEyeLocation().distance(dwarf.getLocation()) <= 2) {
					double damageAmt = 20 + dwarf.getBonusMeleeDamage();
					if (monster.isAI()) damageAmt *= 2;
					MonsterDamage damage = monster.createDamage(dwarf, GameDamageType.TEMPORARY, damageAmt);
					damage.setProc(dwarf.hasProc());
					damage.fire();
					
					damagedEntities.add(monster);
				}
			}
		}
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (isMeleeDamageFromItem(damage)) {
			if (stacks < MAX_STACKS) {
				MonsterEntity monster = damage.getMonster();
				if (monster.isAI()) damage.addPostDamageHandler(() -> stacks++);
			}
		}
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		if (Misc.isRightClick(action) && isHoldingItem() && canJump()) {
			leapCD.tryUse();
		}
		return false;
	}
	
	private void leap() {
		stacks -= JUMP_COST;
		damagedEntities.clear();
		
		dwarf.leap(1, 0.3);
		dwarf.playSound("entity.zombie.attack_iron_door", 1f, 1.5f, true);
		
		dwarf.getWorld().spawnParticle(Particle.CLOUD, dwarf.getLocation(), 10, 0.5, 0.3, 0.5, 0.02);
		
		Player player = dwarf.getPlayer();
		float fall = player.getFallDistance();
		player.setFallDistance(fall/2);
	}
	
	@Override
	public void onShift(boolean sneaking) {
		super.onShift(sneaking);
		stacks++;
	}
	
	private boolean canJump() {
		return stacks >= JUMP_COST;
	}
	
	@Override
	public float getCooldown() {
		int numJumps = stacks/JUMP_COST;
		return (float) numJumps/MAX_JUMPS;
	}
}
