package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 15/01/18.
 */
public class MultipleCooldown implements Cooldown{
	private int maxCD;
	private int cd;
	private final int useCost;
	
	private final Runnable useAction;
	private final Runnable cooldownAction;
	
	public static final Runnable DO_NOTHING = () -> {};
	
	public MultipleCooldown(int maxCD) {
		this(maxCD, DO_NOTHING, DO_NOTHING);
	}
	
	public MultipleCooldown(int maxCD, Runnable useAction) {
		this(maxCD, useAction, DO_NOTHING);
	}
	
	public MultipleCooldown(int maxCD, Runnable useAction, Runnable cooldownAction) {
		this(maxCD, maxCD, useAction, cooldownAction);
	}
	
	public MultipleCooldown(int maxCD, int useCost, Runnable useAction, Runnable cooldownAction) {
		this.cd = 0;
		this.maxCD = maxCD;
		this.useCost = useCost;
		
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
		if (cd > 0) {
			cd--;
			
			if (cd == 0) cooldownAction.run();
		}
	}
	
	public boolean isAvailable() {
		return cd + useCost <= maxCD;
	}
	
	public boolean tryUse() {
		if (isAvailable()) {
			useAction.run();
			cd = Math.min(cd + useCost, maxCD);
			return true;
		}
		
		return false;
	}
	
	public void reduceCooldown(int amt) {
		if (isAvailable()) return;
		
		cd -= amt;
		if (cd <= 0) {
			cooldownAction.run();
			cd = 0;
		}
	}
	
	public void reset() {
		cd = maxCD;
	}
	
	public void stop() {
		cd = 0;
	}
	
	public float getCooldown() {
		return 1 - (float) cd/maxCD;
	}
	
	public boolean wasUsedWithin(int time) {
		return (cd >= maxCD - time);
	}
}

