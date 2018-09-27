package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 27/09/18.
 */
public class CompletionCooldown extends AbstractCooldown {
	
	private final Runnable completeTask;
	
	public CompletionCooldown(int maxTime, Runnable completeTask) {
		super(maxTime);
		this.completeTask = completeTask;
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
	protected void onUse() {
	}
}
