package deimophobe.nightfall.cooldown;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 12/12/18.
 */
public class RepeaterCooldown extends AbstractCooldown {
	
	private final Runnable completeTask;
	
	public RepeaterCooldown(int maxTime, Runnable completeTask) {
		super(maxTime);
		this.completeTask = completeTask;
		this.reset();
	}
	
	@Override
	protected void onCooldownCompletion() {
		completeTask.run();
	}
	
	@Override
	protected boolean canUse() {
		return true;
	}
	
	@Override
	protected void onUse() {}
}
