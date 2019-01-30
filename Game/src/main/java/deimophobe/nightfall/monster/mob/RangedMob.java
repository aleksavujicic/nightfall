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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Deimophobe on 27/01/17.
 */
abstract class RangedMob<T extends RangedUpgrades> extends UpgradeableMob<T> {
	private static final String ARROW_NAME = "arrow";

	RangedMob(MonsterPlayer monster, MobType type, Class<T> upgradeClass) {
		super(monster, type, upgradeClass);
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
	public Projectile onBowFire(Arrow arrow, float force) {
		super.onBowFire(arrow, force);
		updateArms(false);
		return arrow;
	}
	
	private void updateArms(boolean swinging) {
		changeDisguiseWatcher(SkeletonWatcher.class, (sw) -> sw.setSwingArms(swinging));
	}
	
	protected final int getBowPower() {
		return getWeapon().getModifierValue(ItemModifierType.POWER);
	}
	
	protected final void giveArrows(int quantity) {
		ItemStack arrows = getArrowItemStack();
		if (arrows == null || arrows.getType() == Material.AIR) {
			arrows = getItem(ARROW_NAME).createItemStack();
			arrows.setAmount(quantity);
			monster.getPlayer().getInventory().setItemInOffHand(arrows);
		} else {
			int total = quantity + arrows.getAmount();
			if (total > 64) total = 64;
			arrows.setAmount(total);
		}
	}
	protected final boolean hasArrows(int quantity) {
		return hasItem(ARROW_NAME, quantity);
	}
	protected final boolean removeArrows(int quantity) {
		return removeItem(ARROW_NAME, quantity);
	}
	
	private ItemStack getArrowItemStack() {
		return monster.getPlayer().getInventory().getItemInOffHand();
	}
}
