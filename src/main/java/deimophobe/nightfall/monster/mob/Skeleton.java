package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.NaturalDamageType;
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
	protected double realArrowRes = 0;

	public Skeleton(MonsterPlayer mons) {
		this(mons, MobType.SKELETON.getMobData());
	}

	protected Skeleton(MonsterPlayer mons, MobData skeletonData) {
		super(mons, MobType.SKELETON, skeletonData);
		upgrades = monster.getUpgrades(MobType.SKELETON);

		int health = (upgrades.get("health") + upgrades.get("health-inf"));
		getArmour().addModifier(ItemModifierType.HEALTH, health, "Upgrade");
		getArmour().addModifier(ItemModifierType.SPEED, -10, "Skeleton");
		getWeapon().addModifier(ItemModifierType.POWER, getPower());
		getWeapon().addModifier(ItemModifierType.ARMOUR_SHRED, getArmourShred());
	}

	@Override
	public void onSpawn() {
		super.onSpawn();
		giveArrows(64);
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
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.getType() == NaturalDamageType.RANGED && damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) {
			damage.setArmourShred(getArmourShred());
		}
	}

	@Override
	public void onDamageReceive(MonsterDamage damage) {
		super.onDamageReceive(damage);
		damage.getArrowRes().addBoost(realArrowRes);
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

	protected int getPower() {
		return 15 + (upgrades.get("power") + upgrades.get("power-inf")) * 3;
	}

	protected int getArmourShred() {
		return 5 + (upgrades.get("power") + upgrades.get("power-inf")) * 5;
	}
}
