package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.monster.MonsterPlayer;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;

import java.util.Map;

/**
 * Created by Deimophobe on 27/01/17.
 */
class Skeleton extends AbstractMob {

	protected Map<String, Integer> upgrades;

	public Skeleton(MonsterPlayer mons) {
		this(mons, MobType.SKELETON.getMobData());
	}

	protected Skeleton(MonsterPlayer mons, MobData zombieData) {
		super(mons, MobType.SKELETON);
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
		ArrowMisc.setArrowDamage(arrow, getPower());
		return arrow;
	}
	
	protected void giveArrows(int quantity) {
		giveItem("arrow", quantity);
	}
	
	protected double getPower() {
		return 15;
	}
}
