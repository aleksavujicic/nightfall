package deimophobe.nightfall.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 1/02/18.
 */
@Deprecated
public class MultiEventCooldown implements Cooldown {
	private int maxCD;
	private int cd;
	
	private final Supplier<Boolean> useAction;
	private final Map<Integer, Runnable> events = new HashMap<>();
	
	public static final Supplier<Boolean> DO_NOTHING = () -> true;
	private static Supplier<Boolean> wrapRunnable(Runnable runnable) {
		return () -> {
			runnable.run();
			return true;
		};
	}
	
	public MultiEventCooldown(int maxCD, Runnable useAction) {
		this(maxCD, wrapRunnable(useAction));
	}
	
	public MultiEventCooldown(int maxCD, Supplier<Boolean> useAction) {
		this.cd = -1;
		this.maxCD = maxCD;
		
		this.useAction = (useAction == null ? DO_NOTHING : useAction);
	}
	
	public int getMaxCD() {
		return maxCD;
	}
	public void setMaxCD(int newCD) {
		maxCD = newCD;
	}
	
	public void addEvent(int time, Runnable event) {
		events.put(time, event);
	}
	
	public void update() {
		if (cd > 0) {
			cd--;
			
			Runnable event = events.get(cd);
			if (event != null) event.run();
		}
	}
	
	public boolean isAvailable() {
		return cd <= 0;
	}
	
	@Override
	public void forceAvailable() {
		throw new UnsupportedOperationException("Deimo didn't add this cause hes a lazy pos.");
	}
	
	public boolean tryUse() {
		if (isAvailable() && useAction.get()) {
			reset();
			return true;
		} else {
			return false;
		}
	}
	
	public void reset() {
		cd = maxCD;
	}
	
	public float getCooldown() {
		if (cd == -1)
			return 1;
		else
			return 1 - (float) cd/maxCD;
	}
}
