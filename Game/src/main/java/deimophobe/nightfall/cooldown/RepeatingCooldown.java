package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 16/11/17.
 */
@Deprecated
public class RepeatingCooldown extends ComplexCooldown {
	
	public RepeatingCooldown(int maxCD, Runnable useAction) {
		super(maxCD, useAction);
	}
	
	@Override
	public void update() {
		super.update();
		tryUse();
	}
}
