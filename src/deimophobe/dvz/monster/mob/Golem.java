package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Golem extends Mob {
	Golem(MonsterPlayer monster) {
		super(monster, MobType.GOLEM);
	}
	
	
	private static final Material[] UNBREAKABLE_BLOCKS = {
			Material.AIR
	};
	
	private static final int BREAK_CD_MAX = 10;
	private int breakCD = 0;
	
	@Override
	public void onUse(Action action, Block clickedBlock) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (breakCD == 0 && isPlayerHoldingItem(0)) {
				Set<Material> materials = new HashSet<>();
				materials.add(Material.WATER);
				materials.add(Material.STATIONARY_WATER);
				materials.add(Material.LAVA);
				materials.add(Material.STATIONARY_LAVA);
				materials.add(Material.AIR);
				Block block = monster.getTargetBlock(materials, 5);
				
				boolean toBreak = true;
				for (Material unbreakable : UNBREAKABLE_BLOCKS) {
					if (unbreakable == block.getType()) {
						toBreak = false;
						break;
					}
				}
				
				if (toBreak) {
					monster.playSound("entity.generic.explode", 3, 0.5f, true);
					breakCD = BREAK_CD_MAX;
					
					block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0.5, 0.5), 40, 0.5, 0.5, 0.5, 0, block.getState().getData());
					block.breakNaturally ();
				}
			}
		}
	}
	
	@Override
	public void update() {
		breakCD--;
		if (breakCD <= 0) breakCD = 0;
	}
}
