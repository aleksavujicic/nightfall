package deimophobe.nightfall.damage;

import javax.annotation.Nonnull;

/**
 * Created by Deimophobe on 14/02/18.
 */
class DamageHandler<T extends Comparable<T>> implements Comparable<DamageHandler<T>> {
	private final T priority;
	private final Runnable handler;
	
	DamageHandler(T priority, Runnable handler) {
		this.priority = priority;
		this.handler = handler;
	}
	
	public void run() {
		handler.run();
	}
	
	@Override
	public int compareTo(@Nonnull DamageHandler<T> other) {
		return priority.compareTo(other.priority);
	}
}
