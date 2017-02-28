package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import deimophobe.dvz.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 27/01/17.
 */
abstract class SkeletonMob extends Mob {
	
	protected final int power;
	protected SkeletonMob(MonsterPlayer mons, MobType type, int power) {
		super(mons, type);
		this.power = power;
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (DwarvenItem.isRightClick(action)) {
			if (isPlayerHoldingItem(0)) {
				((SkeletonWatcher) getDisguise().getWatcher()).setSwingArms(true);
			} else {
				((SkeletonWatcher) getDisguise().getWatcher()).setSwingArms(false);
			}
		}
	}
	
	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		((SkeletonWatcher) getDisguise().getWatcher()).setSwingArms(false);
		return arrow;
	}
	
	@Override
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (type.isRanged())
			return power;
		else
			return damage;
	}
}
