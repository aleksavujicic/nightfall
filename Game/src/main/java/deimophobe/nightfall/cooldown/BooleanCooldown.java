package deimophobe.nightfall.cooldown;

import java.util.function.Supplier;

//TODO RENAME

/**
 * Created by Deimophobe on 2/06/17.
 */
public class BooleanCooldown implements Cooldown{
	private int maxCD;
	private int cd;
	
	private final Supplier<Boolean> useAction;
	private final Runnable cooldownAction;
	
	public BooleanCooldown(int maxCD) {
		this(maxCD, null, null);
	}
	
	public BooleanCooldown(int maxCD, Supplier<Boolean> useAction) {
		this(maxCD, useAction, null);
	}
	
	public BooleanCooldown(int maxCD, Supplier<Boolean> useAction, Runnable cooldownAction) {
		this.cd = -1;
		this.maxCD = maxCD;
		
		this.useAction = (useAction == null ? () -> true : useAction);
		this.cooldownAction = (cooldownAction == null ? () -> {} : cooldownAction);
	}

	public int getMaxCD() {
		return maxCD;
	}

	public void setMaxCD(int newCD) {
		maxCD = newCD;
	}

	public void update() {
		if (cd > 0)
			cd--;
		
		if (cd == 0) {
			cd = -1;
			cooldownAction.run();
		}
	}
	
	public boolean isAvailable() {
		return cd == -1;
	}
	
	public boolean tryUse() {
		if (isAvailable()) {
			boolean success = useAction.get();
			if (success) {
				reset();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void reduceCooldown(int amt) {
		if (isAvailable()) return;
		
		cd -= amt;
		if (cd <= 0) {
			cooldownAction.run();
			cd = -1;
		}
	}
	
	public void reset() {
		cd = maxCD;
	}
	
	public void stop() {
		cd = -1;
	}
	
	public float getCooldown() {
		if (cd == -1)
			return 1;
		else
			return 1 - (float) cd/maxCD;
	}
	
	public boolean wasUsedWithin(int time) {
		return (cd >= maxCD - time);
	}
}
