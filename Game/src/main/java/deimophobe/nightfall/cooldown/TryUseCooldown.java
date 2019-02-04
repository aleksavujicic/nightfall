package deimophobe.nightfall.cooldown;

import java.util.function.Supplier;

/**
 * Created by Deimophobe on 4/02/19.
 */
public class TryUseCooldown extends AbstractCooldown {
	
	private final Supplier<Boolean> useTask;
	
	public TryUseCooldown(int maxTime, Supplier<Boolean> useTask) {
		super(maxTime);
		this.useTask = useTask;
	}
	
	@Override
	protected void onCooldownCompletion() {
	
	}
	
	@Override
	protected boolean canUse() {
		return useTask.get();
	}
	
	@Override
	protected void onUse() {
	}
}
