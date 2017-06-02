package deimophobe.dvz.cooldown;


//TODO RENAME
/**
 * Created by Deimophobe on 2/06/17.
 */
public class ComplexCooldown {
	private final int maxCD;
	private int cd;
	
	private final Runnable useAction;
	private final Runnable cooldownAction;
	
	public static final Runnable DO_NOTHING = () -> {};
	
	public ComplexCooldown(int maxCD) {
		this(maxCD, DO_NOTHING, DO_NOTHING);
	}
	
	public ComplexCooldown(int maxCD, Runnable useAction, Runnable cooldownAction) {
		this.cd = 0;
		this.maxCD = maxCD;
		
		this.useAction = useAction;
		this.cooldownAction = cooldownAction;
	}
	
	public void update() {
		if (cd > 0)
			cd--;
		
		if (cd == 0)
			cooldownAction.run();
	}
	
	public boolean isAvailable() {
		return cd == 0;
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
			cd = 0;
		}
	}
	
	public void reset() {
		cd = maxCD;
	}
	
	public float fractionComplete() {
		return 1 - (float) cd/maxCD;
	}
}
