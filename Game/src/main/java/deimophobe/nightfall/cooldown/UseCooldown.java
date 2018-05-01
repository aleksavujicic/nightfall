package deimophobe.nightfall.cooldown;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 1/05/18.
 */
public class UseCooldown extends AbstractCooldown {
	
	private final Runnable useTask;
	private final Supplier<Boolean> useCondition;
	
	private static final Runnable DO_NOTHING = () -> {};
	private static final Supplier<Boolean> ALWAYS_OK = () -> true;
	
	public UseCooldown(int maxTime, Runnable useTask) {
		this(maxTime, useTask, ALWAYS_OK);
	}
	
	public UseCooldown(int maxTime, Runnable useTask, Supplier<Boolean> useCondition) {
		super(maxTime);
		this.useTask = useTask;
		this.useCondition = useCondition;
	}
	
	@Override
	protected void onCooldownCompletion() {
	
	}
	
	@Override
	protected boolean canUse() {
		return useCondition.get();
	}
	
	@Override
	protected void onUse() {
		useTask.run();
	}
}
