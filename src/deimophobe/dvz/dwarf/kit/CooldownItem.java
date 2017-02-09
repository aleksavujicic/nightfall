package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.DwarvenItem;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
public abstract class CooldownItem extends DwarvenItem {
	
	protected final int maxCooldown;
	protected int cooldown;
	
	public CooldownItem(Dwarf dwarf, ItemStack item, int maxCooldown) {
		super(dwarf, item);
		if (false) {
			this.maxCooldown = (int) (maxCooldown * 0.8f);
		} else {
			this.maxCooldown = maxCooldown;
		}
		
		cooldown = 0;
		if (maxCooldown == -1) cooldown = -1;
	}
	
	public final void resetCooldown() {
		cooldown = maxCooldown;
		dwarf.updateManaBar();
	}
	
	public final void reduceCooldown(int amount) {
		if (maxCooldown != -1 && cooldown != 0) {
			cooldown -= amount;
			if (cooldown <= 0) {
				playOffCDSound();
				cooldown = 0;
			}
		}
	}
	
	@Override
	public void update() {
		reduceCooldown(1);
	}
	
	@Override
	public boolean use(Action type) {
		boolean success = super.use(type);
		if (success) resetCooldown();
		return success;
	}
	
	@Override
	public float fractionComplete() {
		if (maxCooldown == -1) return -1;
		return 1 - ((float)cooldown/maxCooldown);
	}
	
	@Override
	protected boolean canUse(Action type) {
		return (maxCooldown != -1 && cooldown == 0);
	}
	
	protected void playOffCDSound() {}
}
