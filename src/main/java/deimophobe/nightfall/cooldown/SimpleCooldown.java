package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class SimpleCooldown implements Cooldown {
	private final int maxCD;
	private int cd;
	
	public SimpleCooldown(int maxCD) {
		this.maxCD = maxCD;
		this.cd = 0;
	}
	
	@Override
	public void update() {
		if (cd > 0)
			cd--;
	}
	
	@Override
	public boolean isAvailable() {
		return cd == 0;
	}
	
	@Override
	public void reset() {
		cd = maxCD;
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float) cd/maxCD;
	}
	
}
