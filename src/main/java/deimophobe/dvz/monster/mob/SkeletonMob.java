package deimophobe.dvz.monster.mob;

import deimophobe.dvz.Misc;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.items.modifiers.ItemModifierType;
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
abstract class SkeletonMob extends AbstractTypedMob {
	
	protected SkeletonMob(MonsterPlayer mons) {
		super(mons);
		getWeapon().addModifier(ItemModifierType.POWER, (int) getPower());
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			if (isPlayerHoldingWeapon()) {
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
		if (type.isArrow())
			return getPower();
		else
			return damage;
	}
	
	protected void giveArrows(int quantity) {
		giveItem("arrow", quantity);
	}
	
	protected abstract double getPower();
}
