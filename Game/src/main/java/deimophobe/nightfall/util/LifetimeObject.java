package deimophobe.nightfall.util;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 20/11/17.
 * @deprecated Use {@link deimophobe.nightfall.cooldown.LifetimeExpireable} instead. BukkitRunnables cause headaches.
 */
@Deprecated
public abstract class LifetimeObject extends BukkitRunnable {
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
	
	protected boolean everyNthTick(int n) {
		return lifetime % n == 0;
	}
}