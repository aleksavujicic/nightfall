package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.kit.KitCooldownElement;
import org.bukkit.Bukkit;

/**
 * Created by Deimophobe on 24/03/17.
 */
public abstract class AbstractCooldown extends AbstractElement implements KitCooldownElement {
	
	private final int maxCooldown;
	private int cooldown;
	
	public AbstractCooldown(Dwarf dwarf, int maxCooldown) {
		super(dwarf);
		this.maxCooldown = maxCooldown;
		this.cooldown = 0;
	}
	
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		reduceCooldown();
	}
	
	
	@Override
	public float fractionComplete() {
		if (maxCooldown == -1) return -1;
		return 1 - ((float)cooldown/maxCooldown);
	}
	
	
	protected final void resetCooldown() {
		cooldown = maxCooldown;
	}
	
	protected final void reduceCooldown() {
		reduceCooldown(1);
	}
	
	protected final void reduceCooldown(int amount) {
		if (maxCooldown != -1 && cooldown != 0) {
			cooldown -= amount;
			if (cooldown <= 0) {
				cooldown = 0;
				onOffCD();
			}
		}
	}
	
	protected void increaseCooldown(int amount) {
		if (maxCooldown != -1) {
			cooldown += amount;
			if (cooldown >= maxCooldown) {
				cooldown = maxCooldown;
			}
		}
		
	}
	
	protected final boolean isOffCD() {
		return (cooldown == 0);
	}
	protected final int getCooldown() {
		return cooldown;
	}
	
	protected void onOffCD() {}
	
}
