package deimophobe.nightfall.cooldown;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 3/02/19.
 */
public class VariableRepeaterCooldown extends AbstractCooldown {
	
	private final Runnable useTask;
	private final Supplier<Boolean> resetable;
	
	public VariableRepeaterCooldown(int maxTime, Runnable useTask, Supplier<Boolean> resetable) {
		super(maxTime);
		this.useTask = useTask;
		this.resetable = resetable;
		this.reset();
	}
	
	@Override
	protected void onCooldownCompletion() {
		if (resetable.get()) {
			this.reset();
		}
	}
	
	@Override
	protected boolean canUse() {
		return true;
	}
	
	@Override
	protected void onUse() {
		useTask.run();
	}
}
