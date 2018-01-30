package deimophobe.nightfall.cooldown;

//TODO RENAME
/**
 * Created by Deimophobe on 2/06/17.
 */
public class ComplexCooldown implements Cooldown {
	private int maxCD;
	private int cd;
	
	private final Runnable useAction;
	private final Runnable cooldownAction;
	
	public static final Runnable DO_NOTHING = () -> {};
	
	public ComplexCooldown(int maxCD) {
		this(maxCD, DO_NOTHING, DO_NOTHING);
	}
	
	public ComplexCooldown(int maxCD, Runnable useAction) {
		this(maxCD, useAction, DO_NOTHING);
	}
	
	public ComplexCooldown(int maxCD, Runnable useAction, Runnable cooldownAction) {
		this.cd = -1;
		this.maxCD = maxCD;
		
		this.useAction = (useAction == null ? DO_NOTHING : useAction);
		this.cooldownAction = (cooldownAction == null ? DO_NOTHING : cooldownAction);
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
			useAction.run();
			reset();
			return true;
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
	
	public float fractionComplete() {
		if (cd == -1)
			return 1;
		else
			return 1 - (float) cd/maxCD;
	}
	
	public boolean wasUsedWithin(int time) {
		return (cd >= maxCD - time);
	}
}
