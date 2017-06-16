package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Deimophobe on 16/06/17.
 */
public abstract class AbstractToggleBow extends AbstractBow {
	public AbstractToggleBow(Dwarf dwarf) {super(dwarf);}
	private boolean active = false;
	private final static String ARROW_METADATA_KEY = "active";
	
	@Override
	public Projectile onBowFire(Projectile arrow, float force) {
		arrow = super.onBowFire(arrow, force);
		if (active) {
			arrow.setMetadata(ARROW_METADATA_KEY, new FixedMetadataValue(Game.getGame().getPlugin(), true));
		}
		return arrow;
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isLeftClick(action) && canToggle()) {
			setActive(!active);
			return true;
		}
		return false;
	}
	
	protected void setActive(boolean setActive) {
		for (ItemStack item : dwarf.getPlayer().getInventory().getStorageContents()) {
			if (!matchesItem(item)) continue;
			
			if (setActive)
				item.addEnchantment(Enchantment.DURABILITY, 1);
			else
				item.removeEnchantment(Enchantment.DURABILITY);
		}
		dwarf.getPlayer().updateInventory();
		active = setActive;
	}
	
	protected boolean isActive() {
		return active;
	}
	
	protected boolean isActiveProjectile(Projectile proj) {
		return proj.hasMetadata(ARROW_METADATA_KEY);
	}
	
	protected abstract boolean canToggle();
}
