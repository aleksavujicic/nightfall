package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.upgrades.wrappers.RangedUpgrades;
import deimophobe.nightfall.monster.upgrades.wrappers.WrappedUpgrades;
import deimophobe.nightfall.util.ArrowMisc;
import me.libraryaddict.disguise.disguisetypes.watchers.SkeletonWatcher;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

import java.util.Map;

/**
 * Created by Deimophobe on 27/01/17.
 */
abstract class RangedMob<T extends RangedUpgrades> extends UpgradeableMob<T> {
	private static final String ARROW_NAME = "arrow";
	
	private final double power;
	private final int armourShred;

	RangedMob(MonsterPlayer monster, MobType type, Class<T> upgradeClass) {
		super(monster, type, upgradeClass);
		
		RangedUpgrades upgrades = getUpgrades();
		
		this.power = upgrades.getPower();
		this.armourShred = upgrades.getArmourShred();
	}
	
	@Override
	protected void setupItems() {
		super.setupItems();
		
		int arrows = getUpgrades().getArrowQuantity();
		giveArrows(arrows);
	}
	
	@Override
	public void onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (click.isRightClick()) {
			updateArms(isPlayerHoldingWeapon());
		}
	}

	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if ((damage.getType() == GameDamageType.RANGED && damage.hasArrow() && ArrowMisc.getArrowForce(damage.getArrow()) > 0.7) || damage.getType() == GameDamageType.WITHER_SKULL) {
			damage.setArmourShred(getArmourShred());
		}
	}

	@Override
	public Projectile onBowFire(Arrow arrow, float force) {
		updateArms(false);
		ArrowMisc.setArrowDamage(arrow, getPower());
		return arrow;
	}
	
	private void updateArms(boolean swinging) {
		changeDisguiseWatcher(SkeletonWatcher.class, (sw) -> sw.setSwingArms(swinging));
	}
	
	protected final void giveArrows(int quantity) {
		giveItem(ARROW_NAME, quantity);
	}
	protected final boolean hasArrows(int quantity) {
		return hasItem(ARROW_NAME, quantity);
	}
	protected final boolean removeArrows(int quantity) {
		return removeItem(ARROW_NAME, quantity);
	}

	protected final double getRawPower() {
		return power;
	}
	protected final int getRawArmourShred() {
		return armourShred;
	}
	
	protected double getPower() {
		return getRawPower();
	}
	protected int getArmourShred() {
		return getRawArmourShred();
	}
}
