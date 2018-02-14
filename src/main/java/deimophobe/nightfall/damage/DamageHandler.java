package deimophobe.nightfall.damage;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 14/02/18.
 */
public class DamageHandler<T> implements Comparable<DamageHandler<T>> {
	private final int priority;
	private final Consumer<T> handler;
	
	public DamageHandler(int priority, Consumer<T> handler) {
		this.priority = priority;
		this.handler = handler;
	}
	
	public void consume(T damage) {
		handler.accept(damage);
	}
	
	@Override
	public int compareTo(@Nonnull DamageHandler<T> damageHandler1) {
		return this.priority - damageHandler1.priority;
	}
	
	public static int SAFETY_JUICE_PRIORITY = 10;
	public static int RESURRECTION_PRIORITY = 100;
}
