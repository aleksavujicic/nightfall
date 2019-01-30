package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 11/01/19.
 */
public class FailableCooldown extends UseCooldown {
	private final Runnable failTask;
	
	public FailableCooldown(int maxTime, Runnable useTask, Runnable failTask) {
		super(maxTime, useTask);
		this.failTask = failTask;
	}
	
	@Override
	public boolean tryUse() {
		boolean used = super.tryUse();
		if (!used) failTask.run();
		return used;
	}
}
