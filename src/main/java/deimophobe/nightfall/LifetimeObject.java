package deimophobe.nightfall;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 20/11/17.
 */
public class LifetimeObject extends BukkitRunnable {
	private int lifetime;
	private final int updateFreq;
	
	protected LifetimeObject(int lifetime, int frequency) {
		this.lifetime = lifetime;
		this.updateFreq = frequency;
		
		runTaskTimer(NightfallPlugin.getPlugin(), 0, frequency);
	}
	
	@Override
	public void run() {
		lifetime -= updateFreq;
		if (lifetime <= 0)
			this.cancel();
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		super.cancel();
		lifetime = 0;
	}
	
	protected int getLifeLeft() {
		return lifetime;
	}
}