package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.cooldown.Cooldown;
import deimophobe.nightfall.cooldown.Update;
import deimophobe.nightfall.cooldown.UseCooldown;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.upgrades.wrappers.RangedUpgrades;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 12/02/18.
 */
abstract class AbstractToggleSkeleton<T extends RangedUpgrades> extends RangedMob<T> {
	
	private boolean toggled = false;
	@Update
	private final Cooldown toggler = new UseCooldown(4, this::toggleBow);
	
	AbstractToggleSkeleton(MonsterPlayer monster, MobType type, Class<T> upgradeClass) {
		super(monster, type, upgradeClass);
	}
	
	@Override
	public void onUse(ClickType click, @Nullable Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		if (isPlayerHoldingWeapon() && click.isLeftClick()) {
			toggler.tryUse();
		}
	}
	
	protected final void toggleBow() {
		if (canToggle()) {
			toggled = !toggled;
			setShiny(toggled);
		}
	}
	
	protected final void forceBowToggle(boolean toggle) {
		toggled = toggle;
		setShiny(toggled);
	}
	
	protected final void checkToggle() {
		if (!canToggle()) {
			forceBowToggle(false);
		}
	}
	
	private void setShiny(boolean shiny) {
		for (ItemStack item : monster.getPlayer().getInventory().getStorageContents()) {
			trySetShiny(item, shiny);
		}
		trySetShiny(monster.getPlayer().getItemOnCursor(), shiny);
		
		monster.getPlayer().updateInventory();
	}
	
	private void trySetShiny(ItemStack item, boolean shiny) {
		if (!getWeapon().isSimilar(item)) return;
		
		if (shiny)
			item.addEnchantment(Enchantment.DURABILITY, 1);
		else
			item.removeEnchantment(Enchantment.DURABILITY);
	}
	
	protected final boolean isToggled() {
		return toggled;
	}
	
	protected abstract boolean canToggle();
}
