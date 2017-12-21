package deimophobe.nightfall.cooldown;

import java.util.function.Consumer;

//TODO RENAME

/**
 * Created by Deimophobe on 2/06/17.
 */
public class ConsumerCooldown<T> {
	private int maxCD;
	private int cd;
	
	private final Consumer<T> useAction;
	private final Runnable cooldownAction;
	
	public static final Runnable DO_NOTHING = () -> {};
	
	public ConsumerCooldown(int maxCD, Consumer<T> useAction) {
		this(maxCD, useAction, DO_NOTHING);
	}
	
	public ConsumerCooldown(int maxCD, Consumer<T> useAction, Runnable cooldownAction) {
		this.cd = -1;
		this.maxCD = maxCD;
		
		if (useAction == null) throw new NullPointerException("Cannot have a ConsumerCooldown with no use action");
		this.useAction = useAction;
		this.cooldownAction = (cooldownAction == null ? DO_NOTHING : cooldownAction);
	}

	public int getMaxCD() {
		return maxCD;
	}

	public void setMaxCD(int newCD) {
		maxCD = newCD;
	}

	public void update() {
		if (cd > 0)
			cd--;
		
		if (cd == 0) {
			cd = -1;
			cooldownAction.run();
		}
	}
	
	public boolean isAvailable() {
		return cd == -1;
	}
	
	public boolean tryUse(T object) {
		if (isAvailable()) {
			useAction.accept(object);
			reset();
			return true;
		} else {
			return false;
		}
	}
	
	public void reduceCooldown(int amt) {
		if (isAvailable()) return;
		
		cd -= amt;
		if (cd <= 0) {
			cooldownAction.run();
			cd = -1;
		}
	}
	
	public void reset() {
		cd = maxCD;
	}
	
	public void stop() {
		cd = -1;
	}
	
	public float fractionComplete() {
		if (cd == -1)
			return 1;
		else
			return 1 - (float) cd/maxCD;
	}
	
	public boolean wasUsedWithin(int time) {
		return (cd >= maxCD - time);
	}
}
