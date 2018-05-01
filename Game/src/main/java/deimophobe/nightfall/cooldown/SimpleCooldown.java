package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class SimpleCooldown extends AbstractCooldown {
	
	public SimpleCooldown(int maxTime) {
		super(maxTime);
	}
	
	@Override
	protected void onCooldownCompletion() {}
	
	@Override
	protected boolean canUse() {
		return true;
	}
	
	@Override
	protected void onUse() {}
	
}
