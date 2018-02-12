package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.util.ArrowMisc;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
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
	protected int quiver;

	public Skeleton(MonsterPlayer mons) {
		this(mons, MobType.SKELETON.getMobData());
	}

	protected Skeleton(MonsterPlayer mons, MobData skeletonData) {
		super(mons, MobType.SKELETON, skeletonData);
		upgrades = monster.getUpgrades(MobType.SKELETON);

		this.quiver = (upgrades.get("quiver") + upgrades.get("quiver-inf"));
		getArmour().addModifier(ItemModifierType.SPEED, -10, "Skeleton");
		getWeapon().addModifier(ItemModifierType.POWER, getPower());
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, getArmourShred());
	}

	@Override
	public void onSpawn() {
		super.onSpawn();
		giveArrows(14);
		giveArrows(10 * quiver);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action)) {
			updateArms(isPlayerHoldingWeapon());
		}
	}
	
	protected void updateArms(boolean swinging) {
		changeDisguiseWatcher(SkeletonWatcher.class, (sw) -> sw.setSwingArms(swinging));
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.getType() == NaturalDamageType.RANGED && damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || damage.getType() == CustomDamageType.WITHER_SKULL) {
			damage.setArmourShred(getArmourShred());
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		updateArms(false);
		ArrowMisc.setArrowDamage(arrow, getPower());
		return arrow;
	}
	
	protected void giveArrows(int quantity) {
		giveItem("arrow", quantity);
	}

	protected int getPower() {
		return 15 + (upgrades.get("power") + upgrades.get("power-inf")) * 2;
	}

	protected int getArmourShred() {
		return 15 + (upgrades.get("power") + upgrades.get("power-inf")) * 2;
	}
}
