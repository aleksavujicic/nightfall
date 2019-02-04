package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 24/03/17.
 *
 * @deprecated To be replaced by cooldown fields. These will allow for more flexibility.
 */
@Deprecated
public abstract class AbstractCooldown extends AbstractPiece implements CooldownPiece {
	
	private final int maxCooldown;
	private int cooldown;
	
	public AbstractCooldown(Dwarf dwarf, KitPieceType type, int maxCooldown) {
		super(dwarf, type);
		this.maxCooldown = maxCooldown;
		this.cooldown = 0;
	}
	
	
	@Override
	public void update() {
		reduceCooldown();
	}
	
	
	@Override
	public float getCooldown() {
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
	protected final int getCooldownInt() { return cooldown; }
	
	protected void onOffCD() {}
	
}
